package ie.dublinmapper.livedata

import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import ie.dublinmapper.domain.service.RxScheduler
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import io.rtpi.api.PredictionLiveData
import io.rtpi.api.StopLocation
import io.rtpi.util.AlphaNumericComparator
import javax.inject.Inject
import timber.log.Timber

class LiveDataViewModel @Inject constructor(
    private val liveDataUseCase: LiveDataUseCase,
    private val scheduler: RxScheduler
) : BaseViewModel<Action, State>() {

    override val initialState = State(isLoading = true)

    private val reducer: Reducer<State, Change> = { state, change ->
        when (change) {
            is Change.GetServiceLocation -> state.copy(
                serviceLocationResponse = change.serviceLocationResponse,
                isFavourite = null
            )
            is Change.GetLiveData -> {
                val newState = state.copy(
                    liveDataResponse = change.liveDataResponse,
                    isFavourite = null,
                    isLoading = false,
                    routeDiscrepancyState = tryLogRouteDiscrepancies(state, change)
                )
                filterRoutes(newState, Change.RouteFilterChange(RouteFilterChangeType.NoChange))
            }
            is Change.FavouriteSaved -> state.copy(
                isFavourite = true
            )
            is Change.FavouriteRemoved -> state.copy(
                isFavourite = false
            )
            is Change.RouteFilterChange -> filterRoutes(state, change)
        }
    }

    private fun filterRoutes(
        state: State,
        change: Change.RouteFilterChange
    ): State {
        val adjustedRouteFilters = state.routeFilters.toMutableSet()
        when (change.type) {
            is RouteFilterChangeType.Add -> adjustedRouteFilters.add(change.type.route)
            is RouteFilterChangeType.Remove -> adjustedRouteFilters.remove(change.type.route)
            is RouteFilterChangeType.Clear -> adjustedRouteFilters.clear()
            is RouteFilterChangeType.NoChange -> { /* do nothing */ }
        }
        return if (state.liveDataResponse is LiveDataPresentationResponse.Data &&
            state.liveDataResponse.liveData.all { it is PredictionLiveData }
        ) {
            if (adjustedRouteFilters.isEmpty()) {
                state.copy(
                    filteredLiveDataResponse = state.liveDataResponse,
                    routeFilters = adjustedRouteFilters
                )
            } else {
                state.copy(
                    filteredLiveDataResponse = LiveDataPresentationResponse.Data(
                        liveData = state.liveDataResponse.liveData
                            .filterIsInstance<PredictionLiveData>()
                            .filter { adjustedRouteFilters.contains(it.routeInfo.route) }
                    ),
                    routeFilters = adjustedRouteFilters
                )
            }
        } else {
            state.copy(routeFilters = adjustedRouteFilters)
        }
    }

    init {
        bindActions()
    }

    private fun bindActions() {
        val getServiceLocationChange = actions.ofType(Action.GetServiceLocation::class.java)
            .switchMap { action ->
                liveDataUseCase.getServiceLocation(
                    action.serviceLocationService,
                    action.serviceLocationId
                )
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<Change> { Change.GetServiceLocation(it) }
            }

        val getLiveDataChange = actions.ofType(Action.GetLiveData::class.java)
            .switchMap { action ->
                liveDataUseCase.getLiveDataStream(
                    action.serviceLocationService,
                    action.serviceLocationId
                )
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<Change> { Change.GetLiveData(it) }
            }

        val saveFavouriteChange = actions.ofType(Action.SaveFavourite::class.java)
            .switchMap { action ->
                liveDataUseCase.saveFavourite(
                    action.serviceLocationService,
                    action.serviceLocationId,
                    action.serviceLocationName
                )
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<Change> { Change.FavouriteSaved }
            }

        val removeFavouriteChange = actions.ofType(Action.RemoveFavourite::class.java)
            .switchMap { action ->
                liveDataUseCase.removeFavourite(
                    action.serviceLocationService,
                    action.serviceLocationId
                )
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<Change> { Change.FavouriteRemoved }
            }

        val routeFilterIntents = actions
            .ofType(Action.RouteFilterIntent::class.java)
            .switchMap { intent ->
                Observable.just(Change.RouteFilterChange(intent.type))
            }

        val allActions = Observable.merge(
            listOf(
                getServiceLocationChange,
                getLiveDataChange,
                saveFavouriteChange,
                removeFavouriteChange,
                routeFilterIntents
            )
        )

        disposables += allActions
            .scan(initialState, reducer)
            .distinctUntilChanged()
            .subscribe(state::postValue, Timber::e)
    }

    private fun tryLogRouteDiscrepancies(
        state: State,
        change: Change.GetLiveData
    ): RouteDiscrepancyState? {
        try {
            val serviceLocationResponse = state.serviceLocationResponse
            val liveDataResponse = change.liveDataResponse
            if (liveDataResponse is LiveDataPresentationResponse.Data &&
                serviceLocationResponse is ServiceLocationPresentationResponse.Data
            ) {
                val serviceLocation = serviceLocationResponse.serviceLocation
                val liveData = liveDataResponse.liveData
                if (serviceLocation is StopLocation &&
                    liveData.all { it is PredictionLiveData }
                ) {
                    val knownRoutes = serviceLocation.routeGroups
                        .flatMap { it.routes }
                        .toSortedSet(AlphaNumericComparator)
                    val routes = liveData.filterIsInstance<PredictionLiveData>()
                        .map { it.routeInfo.route }
                        .toSortedSet(AlphaNumericComparator)

                    val routeDiscrepancyState = state.routeDiscrepancyState?.copy(
                        knownRoutes = state.routeDiscrepancyState.knownRoutes.plus(knownRoutes).toSortedSet(),
                        routes = state.routeDiscrepancyState.routes.plus(routes).toSortedSet()
                    ) ?: RouteDiscrepancyState(
                        knownRoutes = knownRoutes,
                        routes = routes,
                        loggedDiscrepancies = sortedSetOf()
                    )

                    val discrepancies = routeDiscrepancyState.routes
                        .minus(routeDiscrepancyState.knownRoutes)
                        .minus(routeDiscrepancyState.loggedDiscrepancies)
                    return if (discrepancies.isNotEmpty()) {
                        // TODO log to firebase
                        Timber.d(
                            "Unknown route(s) %s found at %s(service=%s, id=%s, name=%s)",
                            discrepancies, serviceLocation.javaClass.simpleName,
                            serviceLocation.service, serviceLocation.id,
                            serviceLocation.name
                        )
                        routeDiscrepancyState.copy(
                            loggedDiscrepancies = routeDiscrepancyState.loggedDiscrepancies.plus(discrepancies).toSortedSet()
                        )
                    } else {
                        routeDiscrepancyState
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed while logging route discrepancies")
        }
        return state.routeDiscrepancyState
    }
}

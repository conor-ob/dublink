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
                state.copy(
                    liveDataResponse = change.liveDataResponse,
                    filteredLiveDataResponse = filterLiveDataResponse(state, change.liveDataResponse),
                    isFavourite = null,
                    isLoading = false,
                    routeDiscrepancyState = tryLogRouteDiscrepancies(state, change)
                )
            }
            is Change.FavouriteSaved -> state.copy(
                isFavourite = true
            )
            is Change.FavouriteRemoved -> state.copy(
                isFavourite = false
            )
            is Change.RouteFiltersCleared -> state.copy()
            is Change.RouteFilterChanged -> filterLiveDataResponse(state, change.route, change.enabled)
        }
    }

    private fun filterLiveDataResponse(
        state: State,
        liveDataPresentationResponse: LiveDataPresentationResponse
    ): LiveDataPresentationResponse {
        return if (liveDataPresentationResponse is LiveDataPresentationResponse.Data &&
            liveDataPresentationResponse.liveData.all { it is PredictionLiveData } &&
            state.routeFilters.isNotEmpty()
        ) {
            val filtered = liveDataPresentationResponse
                .liveData
                .filterIsInstance<PredictionLiveData>()
                .filter { state.routeFilters.contains(it.routeInfo.route) }
            return liveDataPresentationResponse.copy(liveData = filtered)
        } else {
            liveDataPresentationResponse
        }
    }

    private fun filterLiveDataResponse(
        state: State,
        route: String,
        enabled: Boolean
    ): State {
        val amendedRouteFilters = state.routeFilters.toMutableSet()
        if (enabled) {
            amendedRouteFilters.add(route)
        } else {
            amendedRouteFilters.remove(route)
        }
        // TODO what happens when we retoggle a disabled filter? we will need to re fetch
        if (state.liveDataResponse is LiveDataPresentationResponse.Data &&
            state.liveDataResponse.liveData.all { it is PredictionLiveData }
        ) {
            if (amendedRouteFilters.isEmpty()) {
                return state.copy(
                    filteredLiveDataResponse = state.liveDataResponse,
                    routeFilters = amendedRouteFilters
                )
            }
            val filtered = state.liveDataResponse.liveData
                .filterIsInstance<PredictionLiveData>()
                .filter { amendedRouteFilters.contains(it.routeInfo.route) }
            return state.copy(
                filteredLiveDataResponse = LiveDataPresentationResponse.Data(liveData = filtered),
                routeFilters = amendedRouteFilters
            )
        }
        return state.copy(
            routeFilters = amendedRouteFilters
        )
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

        val routeFilterChanges = actions.ofType(Action.RouteFilterToggled::class.java)
            .switchMap { action ->
                Observable.just(true)
                    .map { Change.RouteFilterChanged(action.route, action.enabled) }
            }

        val clearRouteFiltersChange = actions.ofType(Action.ClearRouteFilters::class.java)
            .switchMap { action ->
                liveDataUseCase.clearRouteFilters()
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<Change> { Change.RouteFiltersCleared }
            }

        val allActions = Observable.merge(
            listOf(
                getServiceLocationChange,
                getLiveDataChange,
                saveFavouriteChange,
                removeFavouriteChange,
                clearRouteFiltersChange,
                routeFilterChanges
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

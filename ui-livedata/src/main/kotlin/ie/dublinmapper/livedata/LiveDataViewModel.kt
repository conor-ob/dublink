package ie.dublinmapper.livedata

import com.ww.roxie.Reducer
import ie.dublinmapper.LifecycleAwareViewModel
import ie.dublinmapper.domain.model.getCustomDirections
import ie.dublinmapper.domain.model.getCustomRoutes
import ie.dublinmapper.domain.service.PreferenceStore
import ie.dublinmapper.domain.service.RxScheduler
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import io.rtpi.api.DockLiveData
import io.rtpi.api.PredictionLiveData
import io.rtpi.api.StopLocation
import io.rtpi.util.AlphaNumericComparator
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import timber.log.Timber

class LiveDataViewModel @Inject constructor(
    private val liveDataUseCase: LiveDataUseCase,
    private val preferenceStore: PreferenceStore,
    private val scheduler: RxScheduler
) : LifecycleAwareViewModel<Action, State>() {

    override val initialState = State(
        isLoading = true,
        toastMessage = null,
        serviceLocation = null,
        liveDataResponse = null,
        filteredLiveDataResponse = null,
        activeRouteFilters = emptySet(),
        activeDirectionFilters = emptySet(),
        routeDiscrepancyState = null,
        routeFilterState = null
    )

    private val reducer: Reducer<State, Change> = { state, change ->
        when (change) {
            is Change.GetServiceLocation -> State(
                serviceLocation = change.serviceLocation,
                activeRouteFilters = change.serviceLocation.getCustomRoutes().flatMap { it.routes }.toSet(),
                activeDirectionFilters = change.serviceLocation.getCustomDirections().toSet(),
                liveDataResponse = state.liveDataResponse,
                filteredLiveDataResponse = state.filteredLiveDataResponse,
                routeDiscrepancyState = state.routeDiscrepancyState,
                routeFilterState = state.routeFilterState,
                isLoading = state.isLoading,
                toastMessage = null
            )
            is Change.GetLiveData -> {
                val newState = State(
                    liveDataResponse = change.liveDataResponse,
                    serviceLocation = state.serviceLocation,
                    activeRouteFilters = state.activeRouteFilters,
                    activeDirectionFilters = state.activeDirectionFilters,
                    filteredLiveDataResponse = state.filteredLiveDataResponse,
                    routeFilterState = state.routeFilterState,
                    isLoading = false,
                    toastMessage = null,
                    routeDiscrepancyState = tryLogRouteDiscrepancies(state, change)
                )
                filterRoutes(newState, Change.RouteFilterChange(RouteFilterChangeType.NoChange))
            }
            is Change.FavouriteSaved -> state.copy(
                serviceLocation = change.serviceLocation,
                toastMessage = "Saved to Favourites",
                liveDataResponse = state.liveDataResponse,
                filteredLiveDataResponse = state.filteredLiveDataResponse,
                activeRouteFilters = state.activeRouteFilters,
                activeDirectionFilters = state.activeDirectionFilters,
                routeFilterState = state.routeFilterState,
                routeDiscrepancyState = state.routeDiscrepancyState,
                isLoading = state.isLoading
            )
            is Change.FavouriteRemoved -> state.copy(
                serviceLocation = change.serviceLocation,
                toastMessage = "Removed from Favourites",
                liveDataResponse = state.liveDataResponse,
                filteredLiveDataResponse = state.filteredLiveDataResponse,
                activeRouteFilters = state.activeRouteFilters,
                activeDirectionFilters = state.activeDirectionFilters,
                routeFilterState = state.routeFilterState,
                routeDiscrepancyState = state.routeDiscrepancyState,
                isLoading = state.isLoading
            )
            is Change.RouteFilterChange -> filterRoutes(state, change)
            is Change.RouteFilterSheetMoved -> state.copy(
                routeFilterState = change.state
            )
            is Change.Error -> state // TODO
        }
    }

    private fun filterRoutes(
        state: State,
        change: Change.RouteFilterChange
    ): State {
        val adjustedRouteFilters = state.activeRouteFilters.toMutableSet()
        val adjustedDirectionFilters = state.activeDirectionFilters.toMutableSet()
        when (change.type) {
            is RouteFilterChangeType.AddRoute -> adjustedRouteFilters.add(change.type.route)
            is RouteFilterChangeType.RemoveRoute -> adjustedRouteFilters.remove(change.type.route)
            is RouteFilterChangeType.AddDirection -> adjustedDirectionFilters.add(change.type.direction)
            is RouteFilterChangeType.RemoveDirection -> adjustedDirectionFilters.remove(change.type.direction)
            is RouteFilterChangeType.Clear -> adjustedRouteFilters.clear()
            is RouteFilterChangeType.NoChange -> { /* do nothing */ }
        }
        return if (state.liveDataResponse is LiveDataPresentationResponse.Data &&
            state.liveDataResponse.liveData.all { it is PredictionLiveData }
        ) {
            if (adjustedRouteFilters.isEmpty() && adjustedDirectionFilters.isEmpty()) {
                state.copy(
                    filteredLiveDataResponse = state.liveDataResponse,
                    activeRouteFilters = adjustedRouteFilters,
                    activeDirectionFilters = adjustedDirectionFilters
                )
            } else {
                state.copy(
                    filteredLiveDataResponse = LiveDataPresentationResponse.Data(
                        liveData = state.liveDataResponse.liveData
                            .filterIsInstance<PredictionLiveData>()
                            .filter {
                                if (adjustedRouteFilters.isEmpty()) {
                                    true
                                } else {
                                    adjustedRouteFilters.contains(it.routeInfo.route)
                                } && if (adjustedDirectionFilters.isEmpty()) {
                                    true
                                } else {
                                    adjustedDirectionFilters.contains(it.routeInfo.direction)
                                }
                            }
                    ),
                    activeRouteFilters = adjustedRouteFilters,
                    activeDirectionFilters = adjustedDirectionFilters
                )
            }
        } else if (state.liveDataResponse is LiveDataPresentationResponse.Data &&
            state.liveDataResponse.liveData.all { it is DockLiveData }) {
            state.copy(
                filteredLiveDataResponse = state.liveDataResponse,
                activeRouteFilters = adjustedRouteFilters
            )
        } else {
            state.copy(
                activeRouteFilters = adjustedRouteFilters,
                activeDirectionFilters = adjustedDirectionFilters
            )
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
                    .map<Change> { response ->
                        when (response) {
                            is ServiceLocationPresentationResponse.Data -> Change.GetServiceLocation(response.serviceLocation)
                            is ServiceLocationPresentationResponse.Error -> Change.Error(response.throwable)
                        }
                    }
            }

        val getLiveDataChange = actions.ofType(Action.GetLiveData::class.java)
            .switchMap { action ->
                Observable.interval(0L, preferenceStore.getLiveDataRefreshInterval(), TimeUnit.SECONDS)
                    .filter { isActive() }
                    .flatMap {
                        liveDataUseCase.getLiveData(
                            action.serviceLocationService,
                            action.serviceLocationId
                        )
                    }
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<Change> { Change.GetLiveData(it) }
            }

        val saveFavouriteChange = actions.ofType(Action.SaveFavourite::class.java)
            .switchMap { action ->
                liveDataUseCase.saveFavourite(action.serviceLocation)
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<Change> { response ->
                        when (response) {
                            is ServiceLocationPresentationResponse.Data -> Change.FavouriteSaved(response.serviceLocation)
                            is ServiceLocationPresentationResponse.Error -> Change.Error(response.throwable)
                        }
                    }
            }

        val removeFavouriteChange = actions.ofType(Action.RemoveFavourite::class.java)
            .switchMap { action ->
                liveDataUseCase.removeFavourite(
                    action.serviceLocationService,
                    action.serviceLocationId
                )
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<Change> { response ->
                        when (response) {
                            is ServiceLocationPresentationResponse.Data -> Change.FavouriteRemoved(response.serviceLocation)
                            is ServiceLocationPresentationResponse.Error -> Change.Error(response.throwable)
                        }
                    }
            }

        val routeFilterIntents = actions
            .ofType(Action.RouteFilterIntent::class.java)
            .switchMap { intent -> Observable.just(Change.RouteFilterChange(intent.type)) }

        val bottomSheetIntents = actions
            .ofType(Action.RouteFilterSheetMoved::class.java)
            .switchMap { intent -> Observable.just(Change.RouteFilterSheetMoved(intent.state)) }

        val allActions = Observable.merge(
            listOf(
                getServiceLocationChange,
                getLiveDataChange,
                saveFavouriteChange,
                removeFavouriteChange,
                routeFilterIntents,
                bottomSheetIntents
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
            val liveDataResponse = change.liveDataResponse
            if (liveDataResponse is LiveDataPresentationResponse.Data && state.serviceLocation != null) {
                val serviceLocation = state.serviceLocation
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

package ie.dublinmapper.livedata

import com.ww.roxie.Reducer
import ie.dublinmapper.LifecycleAwareViewModel
import ie.dublinmapper.domain.model.DubLinkStopLocation
import ie.dublinmapper.domain.model.addFilter
import ie.dublinmapper.domain.model.clearFilters
import ie.dublinmapper.domain.model.removeFilter
import ie.dublinmapper.domain.service.PreferenceStore
import ie.dublinmapper.domain.service.RxScheduler
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import io.rtpi.api.PredictionLiveData
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
        routeDiscrepancyState = null,
        routeFilterState = null
    )

    private val reducer: Reducer<State, Change> = { state, change ->
        when (change) {
            is Change.GetServiceLocation -> State(
                serviceLocation = change.serviceLocation,
                liveDataResponse = state.liveDataResponse,
                routeDiscrepancyState = state.routeDiscrepancyState,
                routeFilterState = state.routeFilterState,
                isLoading = state.isLoading,
                toastMessage = null
            )
            is Change.GetLiveData -> {
                State(
                    liveDataResponse = change.liveDataResponse,
                    serviceLocation = state.serviceLocation,
                    routeFilterState = state.routeFilterState,
                    isLoading = false,
                    toastMessage = null,
                    routeDiscrepancyState = tryLogRouteDiscrepancies(state, change)
                )
            }
            is Change.FavouriteSaved -> State(
                serviceLocation = change.serviceLocation,
                toastMessage = "Saved to Favourites",
                liveDataResponse = state.liveDataResponse,
                routeFilterState = state.routeFilterState,
                routeDiscrepancyState = state.routeDiscrepancyState,
                isLoading = state.isLoading
            )
            is Change.FavouriteRemoved -> State(
                serviceLocation = change.serviceLocation,
                toastMessage = "Removed from Favourites",
                liveDataResponse = state.liveDataResponse,
                routeFilterState = state.routeFilterState,
                routeDiscrepancyState = state.routeDiscrepancyState,
                isLoading = state.isLoading
            )
            is Change.RouteFilterChange -> State(
                serviceLocation = if (state.serviceLocation is DubLinkStopLocation) {
                    when (change.type) {
                        is FilterChangeType.Add -> state.serviceLocation.addFilter(change.type.filter)
                        is FilterChangeType.Remove -> state.serviceLocation.removeFilter(change.type.filter)
                        is FilterChangeType.Clear -> state.serviceLocation.clearFilters()
                    }
                } else {
                    state.serviceLocation
                },
                toastMessage = null,
                liveDataResponse = state.liveDataResponse,
                routeFilterState = state.routeFilterState,
                routeDiscrepancyState = state.routeDiscrepancyState,
                isLoading = state.isLoading
            )
            is Change.RouteFilterSheetMoved -> state.copy(
                routeFilterState = change.state
            )
            is Change.Error -> state // TODO
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
            .ofType(Action.FilterIntent::class.java)
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
                if (serviceLocation is DubLinkStopLocation &&
                    liveData.all { it is PredictionLiveData }
                ) {
                    val knownRoutes = serviceLocation.stopLocation.routeGroups
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

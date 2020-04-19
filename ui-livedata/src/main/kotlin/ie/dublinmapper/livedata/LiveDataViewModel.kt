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
                tryLogRouteDiscrepancies(state, change)
                state.copy(
                    liveDataResponse = change.liveDataResponse,
                    isFavourite = null,
                    isLoading = false
                )
            }
            is Change.FavouriteSaved -> state.copy(
                isFavourite = true
            )
            is Change.FavouriteRemoved -> state.copy(
                isFavourite = false
            )
            is Change.RouteFiltersCleared -> state.copy(
                isLoading = false,
                liveDataResponse = null
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
                clearRouteFiltersChange
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
    ) {
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
                    val discrepancies = routes.minus(knownRoutes)
                    if (discrepancies.isNotEmpty()) {
                        // TODO log to firebase
                        Timber.d(
                            "Unknown route(s) %s found at %s(service=%s, id=%s, name=%s)",
                            discrepancies, serviceLocation.javaClass.simpleName,
                            serviceLocation.service, serviceLocation.id,
                            serviceLocation.name
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed while logging route discrepancies")
        }
    }
}

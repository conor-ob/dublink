package ie.dublinmapper.livedata

import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import ie.dublinmapper.domain.service.RxScheduler
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import io.rtpi.api.PredictionLiveData
import io.rtpi.api.StopLocation
import io.rtpi.util.AlphaNumericComparator
import timber.log.Timber
import javax.inject.Inject

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
//                isFavourite = change.serviceLocationResponse.isFavourite()
            )
            is Change.GetLiveData -> {
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
                    Timber.e(e, "Failed while calculating route discrepancies")
                }
                state.copy(
//                    isLoading = false,
                    liveDataResponse = change.liveDataResponse,
                    isFavourite = null,
                    isLoading = false
//                    isError = false
                )
            }
            is Change.FavouriteSaved -> state.copy(
                isFavourite = true
//                isError = false
            )
            is Change.FavouriteRemoved -> state.copy(
                isFavourite = false
//                isError = false
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
//                    .onErrorReturn {
//                        Timber.e(it)
//                        Change.GetLiveDataError(it)
//                    }
            }

        val getLiveDataChange = actions.ofType(Action.GetLiveData::class.java)
            .switchMap { action ->
                liveDataUseCase.getLiveDataStream(
                    action.serviceLocationService,
                    action.serviceLocationId
                )
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
//                    .map<Group> { LiveDataMapper.map(it) }
                    .map<Change> { Change.GetLiveData(it) }
//                    .onErrorReturn {
//                        Timber.e(it)
//                        Change.GetLiveDataError(it)
//                    }
//                    .startWith(Change.Loading)
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
//                    .onErrorReturn { Change.GetLiveDataError(it) }
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
//                    .onErrorReturn { Change.GetLiveDataError(it) }
            }

        val allActions = Observable.merge(
            getServiceLocationChange,
            getLiveDataChange,
            saveFavouriteChange,
            removeFavouriteChange
        )

        disposables += allActions
            .scan(initialState, reducer)
            .distinctUntilChanged()
            .subscribe(state::postValue, Timber::e)
    }
}

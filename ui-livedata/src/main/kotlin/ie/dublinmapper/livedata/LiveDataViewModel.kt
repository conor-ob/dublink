package ie.dublinmapper.livedata

import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import com.xwray.groupie.Group
import ie.dublinmapper.domain.service.RxScheduler
import ie.dublinmapper.model.LiveDataItem
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import io.rtpi.util.RouteComparator
import timber.log.Timber
import javax.inject.Inject

class LiveDataViewModel @Inject constructor(
    private val liveDataUseCase: LiveDataUseCase,
    private val scheduler: RxScheduler
) : BaseViewModel<Action, State>() {

    override val initialState = State()

    private val reducer: Reducer<State, Change> = { state, change ->
        when (change) {
            is Change.GetServiceLocation -> state.copy(
                serviceLocationResponse = change.serviceLocationResponse
//                isFavourite = change.serviceLocationResponse.isFavourite()
            )
            is Change.GetLiveData -> {
//                val serviceLocation = state.serviceLocationResponse
//                if (serviceLocation != null && serviceLocation is ServiceLocationRoutes) {
//                    try {
//                        val routes = serviceLocation.routes.toSortedSet(RouteComparator)
//                        val itemCount = change.liveDataResponse.itemCount
//                        val currentRoutes = mutableSetOf<Route>()
//                        for (i in 0 until itemCount) {
//                            val item = change.liveDataResponse.getItem(i)
//                            if (item is LiveDataItem) {
//                                currentRoutes.add(Route(item.liveData.route, item.liveData.operator))
//                            }
//                        }
//                        val sorted = currentRoutes.toSortedSet(RouteComparator)
//                        Timber.d(routes.toString())
//                        Timber.d(sorted.toString())
//                        // TODO
//                        // if (there is a route in sorted that is not in routes) {
//                        //     Timber.e(log this)
//                        // }
//                    } catch (e: Exception) {
//                        Timber.e(e, "Failed while calculating route discrepancies")
//                    }
//                }
                state.copy(
//                    isLoading = false,
                    liveDataResponse = change.liveDataResponse
//                    isError = false
                )
            }
            is Change.FavouriteSaved -> state.copy(
//                isFavourite = true,
//                isError = false
            )
            is Change.FavouriteRemoved -> state.copy(
//                isFavourite = false,
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

        val allActions = Observable.merge(getServiceLocationChange, getLiveDataChange, saveFavouriteChange, removeFavouriteChange)

        disposables += allActions
            .scan(initialState, reducer)
            .distinctUntilChanged()
            .subscribe(state::postValue, Timber::e)
    }
}

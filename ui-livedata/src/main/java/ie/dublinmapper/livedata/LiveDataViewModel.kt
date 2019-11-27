package ie.dublinmapper.livedata

import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import com.xwray.groupie.Group
import ie.dublinmapper.domain.model.isFavourite
import ie.dublinmapper.domain.usecase.FavouritesUseCase
import ie.dublinmapper.domain.usecase.LiveDataUseCase
import ie.dublinmapper.util.RxScheduler
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import io.rtpi.api.Route
import io.rtpi.api.ServiceLocationRoutes
import io.rtpi.api.TimedLiveData
import ma.glasnost.orika.MapperFacade
import timber.log.Timber
import javax.inject.Inject

class LiveDataViewModel @Inject constructor(
    private val liveDataUseCase: LiveDataUseCase,
    private val favouritesUseCase: FavouritesUseCase,
    private val mapper: MapperFacade,
    private val scheduler: RxScheduler
) : BaseViewModel<Action, State>() {

    override val initialState = State(isLoading = true)

    private val reducer: Reducer<State, Change> = { state, change ->
        when (change) {
            is Change.Loading -> state.copy(
                isLoading = true,
                liveData = null,
                isError = false
            )
            is Change.GetServicelocation -> state.copy(
                serviceLocation = change.serviceLocation,
                isFavourite = change.serviceLocation.isFavourite()
            )
            is Change.GetLiveData -> {
                val serviceLocation = state.serviceLocation
                if (serviceLocation != null && serviceLocation is ServiceLocationRoutes) {
                    val routes = serviceLocation.routes
//                    val map = change.liveData.filterIsInstance<TimedLiveData>()
//                        .map { Route(it.route, it.operator) }
                    // compare routes with live data and send report if they don't match
                    // val liveData = change.liveData
                }
                state.copy(
                    isLoading = false,
                    liveData = change.liveData,
                    isError = false
                )
            }
            is Change.GetLiveDataError -> state.copy(
                isLoading = false,
                liveData = null,
                isError = true //TODO
            )
            is Change.FavouriteSaved -> state.copy(
                isFavourite = true,
                isError = false
            )
            is Change.FavouriteRemoved -> state.copy(
                isFavourite = false,
                isError = false
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
                    action.serviceLocationId,
                    action.serviceLocationService
                )
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<Change> { Change.GetServicelocation(it) }
                    .onErrorReturn {
                        Timber.e(it)
                        Change.GetLiveDataError(it)
                    }
            }

        val getLiveDataChange = actions.ofType(Action.GetLiveData::class.java)
            .switchMap { action ->
                liveDataUseCase.getLiveDataStream(
                    action.serviceLocationId,
                    action.serviceLocationName,
                    action.serviceLocationService
                )
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<Group> { mapper.map(it, Group::class.java) }
                    .map<Change> { Change.GetLiveData(it) }
                    .onErrorReturn {
                        Timber.e(it)
                        Change.GetLiveDataError(it)
                    }
                    .startWith(Change.Loading)
            }

        val saveFavouriteChange = actions.ofType(Action.SaveFavourite::class.java)
            .switchMap { action ->
                favouritesUseCase.saveFavourite(
                    action.serviceLocationId,
                    action.serviceLocationName,
                    action.serviceLocationService
                )
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<Change> { Change.FavouriteSaved }
                    .onErrorReturn { Change.GetLiveDataError(it) }
            }

        val removeFavouriteChange = actions.ofType(Action.RemoveFavourite::class.java)
            .switchMap { action ->
                favouritesUseCase.removeFavourite(
                    action.serviceLocationId,
                    action.serviceLocationService
                )
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<Change> { Change.FavouriteRemoved }
                    .onErrorReturn { Change.GetLiveDataError(it) }
            }

        val allActions = Observable.merge(getServiceLocationChange, getLiveDataChange, saveFavouriteChange, removeFavouriteChange)

        disposables += allActions
            .scan(initialState, reducer)
//            .filter { !it.isLoading }
            .distinctUntilChanged()
            .subscribe(state::postValue, Timber::e)
    }

}

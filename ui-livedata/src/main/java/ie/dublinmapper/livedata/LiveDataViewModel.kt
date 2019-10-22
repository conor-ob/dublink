package ie.dublinmapper.livedata

import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import com.xwray.groupie.Group
import ie.dublinmapper.domain.usecase.FavouritesUseCase
import ie.dublinmapper.domain.usecase.LiveDataUseCase
import ie.dublinmapper.util.RxScheduler
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
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
            is Change.GetLiveData -> state.copy(
                isLoading = false,
                liveData = change.liveData,
                isError = false
            )
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
                    .toObservable<Change>()
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .doOnNext { Change.FavouriteSaved }
                    .onErrorReturn { Change.GetLiveDataError(it) }
            }

        val removeFavouriteChange = actions.ofType(Action.RemoveFavourite::class.java)
            .switchMap { action ->
                favouritesUseCase.removeFavourite(
                    action.serviceLocationId,
                    action.serviceLocationService
                )
                    .toObservable<Change>()
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .doOnNext { Change.FavouriteRemoved }
                    .onErrorReturn { Change.GetLiveDataError(it) }
            }

        val allActions = Observable.merge(getLiveDataChange, saveFavouriteChange, removeFavouriteChange)

        disposables += allActions
            .scan(initialState, reducer)
//            .filter { !it.isLoading }
            .distinctUntilChanged()
            .subscribe(state::postValue, Timber::e)
    }

}

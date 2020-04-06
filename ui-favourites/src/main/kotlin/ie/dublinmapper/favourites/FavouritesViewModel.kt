package ie.dublinmapper.favourites

import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import ie.dublinmapper.domain.internet.InternetStatusChangeListener
import ie.dublinmapper.domain.service.RxScheduler
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import ma.glasnost.orika.MapperFacade
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class FavouritesViewModel @Inject constructor(
    private val useCase: FavouritesUseCase,
    private val internetStatusChangeListener: InternetStatusChangeListener,
    private val mapper: MapperFacade,
    private val scheduler: RxScheduler
) : BaseViewModel<Action, State>() {

    override val initialState = State(
        favourites = null,
        liveData = null,
        internetStatusChange = null
    )

    private val reducer: Reducer<State, Change> = { state, change ->
        when (change) {
            is Change.GetFavourites -> state.copy(
                favourites = change.favourites,
                internetStatusChange = null
            )
            is Change.GetLiveData -> state.copy(
                liveData = change.liveData,
                internetStatusChange = null
            )
            is Change.InternetStatusChange -> state.copy(
                internetStatusChange = change.internetStatusChange
            )
        }
    }

    init {
        bindActions()
    }

    private fun bindActions() {
        val getFavouritesChange = actions.ofType(Action.GetFavourites::class.java)
            .switchMap { _ ->
                useCase.getFavourites()
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
//                    .map<Group> { mapper.map(it, Group::class.java) }
                    .map<Change> { Change.GetFavourites(it) }
//                    .onErrorReturn {
//                        Timber.e(it)
//                        Change.GetFavouritesError(it)
//                    }
                    .throttleLatest(500L, TimeUnit.MILLISECONDS)
            }

        val getLiveDataChange = actions.ofType(Action.GetLiveData::class.java)
            .switchMap { _ ->
                useCase.getLiveData()
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
//                    .map<Group> { mapper.map(it, Group::class.java) }
                    .map<Change> { Change.GetLiveData(it) }
//                    .onErrorReturn {
//                        Timber.e(it)
//                        Change.GetFavouritesError(it)
//                    }
                    .throttleLatest(500L, TimeUnit.MILLISECONDS)
            }

        val getInternetStatusChange = actions.ofType(Action.SubscribeToInternetStatusChanges::class.java)
            .switchMap {
                internetStatusChangeListener.eventStream()
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<Change> { Change.InternetStatusChange(it) }
            }

        val allActions = Observable.merge(
            getFavouritesChange,
            getLiveDataChange,
            getInternetStatusChange
        )

        disposables += allActions
            .scan(initialState, reducer)
            .distinctUntilChanged()
            .subscribe(state::postValue, Timber::e)
    }
}

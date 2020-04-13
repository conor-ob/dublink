package ie.dublinmapper.favourites

import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import ie.dublinmapper.domain.internet.InternetStatusChangeListener
import ie.dublinmapper.domain.service.RxScheduler
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import timber.log.Timber

class FavouritesViewModel @Inject constructor(
    private val useCase: FavouritesUseCase,
    private val internetStatusChangeListener: InternetStatusChangeListener,
    private val scheduler: RxScheduler
) : BaseViewModel<Action, State>() {

    override val initialState = State(
        favouritesWithLiveData = null,
        internetStatusChange = null
    )

    private val reducer: Reducer<State, Change> = { state, change ->
        when (change) {
            is Change.FavouritesWithLiveData -> state.copy(
                favouritesWithLiveData = change.favouritesWithLiveData,
                internetStatusChange = null
            )
            is Change.InternetStatusChange -> state.copy(
                internetStatusChange = change.internetStatusChange
            )
        }
    }

    fun bindActions() {
        val getFavouritesWithLiveDataChange = actions.ofType(Action.GetFavouritesWithLiveData::class.java)
            .switchMap { action ->
                useCase.getFavouritesWithLiveData(action.showLoading)
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<Change> { Change.FavouritesWithLiveData(it) }
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
            getFavouritesWithLiveDataChange,
            getInternetStatusChange
        )

        disposables += allActions
            .scan(initialState, reducer)
            .distinctUntilChanged()
            .subscribe(state::postValue, Timber::e)
    }

    fun unbindActions() {
        disposables.clear()
    }
}

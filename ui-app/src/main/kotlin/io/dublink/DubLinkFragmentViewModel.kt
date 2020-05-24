package io.dublink

import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import io.dublink.domain.internet.InternetStatusChangeListener
import io.dublink.domain.service.RxScheduler
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject
import timber.log.Timber

class DubLinkFragmentViewModel @Inject constructor(
    private val internetStatusChangeListener: InternetStatusChangeListener,
    private val scheduler: RxScheduler
) : BaseViewModel<Action, State>() {

    override val initialState = State(
        internetStatusChange = null
    )

    private val reducer: Reducer<State, Change> = { _, change ->
        when (change) {
            is Change.InternetStatusChange -> State(
                internetStatusChange = change.internetStatusChange
            )
        }
    }

    init {
        bindActions()
    }

    private fun bindActions() {
        val getInternetStatusChange = actions.ofType(Action.SubscribeToInternetStatusChanges::class.java)
            .switchMap {
                internetStatusChangeListener.eventStream()
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<Change> { type ->
                        Change.InternetStatusChange(type)
                    }
            }

        val allChanges = Observable.merge(
            listOf(
                getInternetStatusChange
            )
        )

        disposables += allChanges
            .scan(initialState, reducer)
            .distinctUntilChanged()
            .subscribe(state::postValue, Timber::e)
    }
}

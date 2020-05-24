package io.dublink

import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import io.dublink.domain.internet.InternetStatusChangeListener
import io.dublink.domain.service.RxScheduler
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import java.time.Instant
import javax.inject.Inject
import timber.log.Timber

class DubLinkFragmentViewModel @Inject constructor(
    private val internetStatusChangeListener: InternetStatusChangeListener,
    private val scheduler: RxScheduler
) : BaseViewModel<DubLinkFragmentAction, DubLinkFragmentState>() {

    override val initialState = DubLinkFragmentState(
        internetStatusChangeEvent = null
    )

    private val reducer: Reducer<DubLinkFragmentState, DubLinkFragmentChange> = { _, change ->
        when (change) {
            is DubLinkFragmentChange.InternetStatusChange -> DubLinkFragmentState(
                internetStatusChangeEvent = InternetStatusChangeEvent(
                    internetStatusChange = change.internetStatusChange,
                    timestamp = Instant.now()
                )
            )
        }
    }

    init {
        bindActions()
    }

    private fun bindActions() {
        val getInternetStatusChange = actions.ofType(DubLinkFragmentAction.SubscribeToInternetStatusChanges::class.java)
            .switchMap {
                internetStatusChangeListener.eventStream()
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<DubLinkFragmentChange> { type ->
                        DubLinkFragmentChange.InternetStatusChange(type)
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

package io.dublink

import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import io.dublink.domain.internet.InternetStatus
import io.dublink.domain.internet.InternetStatusChangeListener
import io.dublink.domain.repository.AggregatedServiceLocationRepository
import io.dublink.domain.service.RxScheduler
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject
import timber.log.Timber

class DubLinkActivityViewModel @Inject constructor(
    private val serviceLocationRepository: AggregatedServiceLocationRepository,
    private val internetStatusChangeListener: InternetStatusChangeListener,
    private val scheduler: RxScheduler
) : BaseViewModel<Action, State>() {

    override val initialState = State(internetStatusChange = null)

    private val reducer: Reducer<State, Change> = { state, change ->
        when (change) {
            is Change.InternetStatusChange -> State(
                internetStatusChange = change.internetStatusChange
            )
            is Change.PreloadChange -> State(
                internetStatusChange = state.internetStatusChange
            )
        }
    }

    init {
        bindActions()
    }

    private fun bindActions() {
        val preloadDataChanges = actions.ofType(Action.PreloadData::class.java)
            .switchMap {
                serviceLocationRepository.get()
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map { Change.PreloadChange }
            }

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
                preloadDataChanges,
                getInternetStatusChange
            )
        )

        disposables += allChanges
            .scan(initialState, reducer)
            .distinctUntilChanged()
            .subscribe(state::postValue, Timber::e)
    }
}

sealed class Action : BaseAction {
    object PreloadData : Action()
    object SubscribeToInternetStatusChanges : Action()
}

sealed class Change {
    object PreloadChange : Change()
    data class InternetStatusChange(val internetStatusChange: InternetStatus) : Change()
}

data class State(
    val internetStatusChange: InternetStatus? = null
) : BaseState

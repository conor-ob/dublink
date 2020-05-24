package io.dublink

import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import io.dublink.domain.internet.InternetStatus
import io.dublink.domain.repository.AggregatedServiceLocationRepository
import io.dublink.domain.service.RxScheduler
import io.dublink.iap.dublinkpro.DubLinkProUseCase
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject
import timber.log.Timber

class DubLinkActivityViewModel @Inject constructor(
    private val serviceLocationRepository: AggregatedServiceLocationRepository,
    private val useCase: DubLinkProUseCase,
    private val scheduler: RxScheduler
) : BaseViewModel<Action, State>() {

    override val initialState = State(internetStatusChange = null)

    private val reducer: Reducer<State, Change> = { state, change ->
        when (change) {
            is Change.Ignored -> state
        }
    }

    init {
        bindActions()
    }

    private fun bindActions() {
        val queryPurchasesActions = actions.ofType(Action.QueryPurchases::class.java)
            .switchMapSingle {
                useCase.getPurchases()
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<Change> { Change.Ignored }
            }

        val preloadDataChanges = actions.ofType(Action.PreloadData::class.java)
            .switchMap {
                serviceLocationRepository.get()
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map { Change.Ignored }
            }

        val allChanges = Observable.merge(
            listOf(
                queryPurchasesActions,
                preloadDataChanges
            )
        )

        disposables += allChanges
            .scan(initialState, reducer)
            .distinctUntilChanged()
            .subscribe(state::postValue, Timber::e)
    }
}

sealed class Action : BaseAction {
    object QueryPurchases : Action()
    object PreloadData : Action()
}

sealed class Change {
    object Ignored : Change()
}

data class State(
    val internetStatusChange: InternetStatus? = null
) : BaseState

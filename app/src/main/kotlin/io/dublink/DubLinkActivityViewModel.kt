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
) : BaseViewModel<DubLinkActivityAction, DubLinkActivityState>() {

    override val initialState = DubLinkActivityState(internetStatusChange = null)

    private val reducer: Reducer<DubLinkActivityState, DubLinkActivityChange> = { state, change ->
        when (change) {
            is DubLinkActivityChange.Ignored -> state
        }
    }

    init {
        bindActions()
    }

    private fun bindActions() {
        val queryPurchasesActions = actions.ofType(DubLinkActivityAction.QueryPurchases::class.java)
            .switchMapSingle {
                useCase.getPurchases()
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<DubLinkActivityChange> { DubLinkActivityChange.Ignored }
            }

        val preloadDataChanges = actions.ofType(DubLinkActivityAction.PreloadData::class.java)
            .switchMap {
                serviceLocationRepository.get()
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map { DubLinkActivityChange.Ignored }
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

sealed class DubLinkActivityAction : BaseAction {
    object QueryPurchases : DubLinkActivityAction()
    object PreloadData : DubLinkActivityAction()
}

sealed class DubLinkActivityChange {
    object Ignored : DubLinkActivityChange()
}

data class DubLinkActivityState(
    val internetStatusChange: InternetStatus? = null
) : BaseState

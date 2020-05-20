package io.dublink.iap.dublinkpro

import android.app.Activity
import com.android.billingclient.api.Purchase
import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import io.dublink.domain.service.RxScheduler
import io.dublink.iap.PurchasesUpdate
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject
import timber.log.Timber

class DubLinkProViewModel @Inject constructor(
    private val useCase: DubLinkProUseCase,
    private val rxScheduler: RxScheduler
) : BaseViewModel<Action, State>() {

    override val initialState = State(
        dubLinkProPrice = null,
        canPurchaseDubLinkPro = null,
        errorMessage = null
    )

    private val reducer: Reducer<State, NewState> = { state, newState ->
        when (newState) {
            is NewState.SkuDetails -> {
                when (newState.skuDetailsResponse) {
                    is SkuDetailsResponse.Data -> State(
                        dubLinkProPrice = newState.skuDetailsResponse.skuDetails.price,
                        errorMessage = null,
                        canPurchaseDubLinkPro = state.canPurchaseDubLinkPro
                    )
                    is SkuDetailsResponse.Error -> State(
                        dubLinkProPrice = state.dubLinkProPrice,
                        errorMessage = newState.skuDetailsResponse.message,
                        canPurchaseDubLinkPro = state.canPurchaseDubLinkPro
                    )
                }
            }
            is NewState.Purchases -> if (newState.purchases.isNullOrEmpty()) {
                State(
                    canPurchaseDubLinkPro = true,
                    dubLinkProPrice = state.dubLinkProPrice,
                    errorMessage = null
                )
            } else {
                State(
                    canPurchaseDubLinkPro = false,
                    dubLinkProPrice = state.dubLinkProPrice,
                    errorMessage = null
                )
            }
            is NewState.PurchaseUpdate -> {
                Timber.d(newState.purchasesUpdate.toString())
                State(
                    canPurchaseDubLinkPro = false,
                    dubLinkProPrice = state.dubLinkProPrice,
                    errorMessage = null
                )
            }
            is NewState.Ignored -> state
        }
    }

    init {
        bindActions()
    }

    private fun bindActions() {
        val observePurchaseUpdatesActions = actions.ofType(Action.ObservePurchaseUpdates::class.java)
            .switchMap {
                useCase.observePurchaseUpdates()
                    .subscribeOn(rxScheduler.io)
                    .observeOn(rxScheduler.ui)
                    .toObservable()
                    .map<NewState> { NewState.PurchaseUpdate(it) }
            }

        val getSkuDetailsActions = actions.ofType(Action.QuerySkuDetails::class.java)
            .switchMapSingle {
                useCase.getDubLinkProSkuDetails()
                    .subscribeOn(rxScheduler.io)
                    .observeOn(rxScheduler.ui)
                    .map<NewState> { response -> NewState.SkuDetails(response) }
            }

        val queryPurchasesActions = actions.ofType(Action.QueryPurchases::class.java)
            .switchMapSingle {
                useCase.getPurchases()
                    .subscribeOn(rxScheduler.io)
                    .observeOn(rxScheduler.ui)
                    .map<NewState> { NewState.Purchases(it) }
            }

        val queryPurchaseHistoryActions = actions.ofType(Action.QueryPurchaseHistory::class.java)
            .switchMapSingle {
                useCase.getPurchaseHistory()
                    .subscribeOn(rxScheduler.io)
                    .observeOn(rxScheduler.ui)
                    .map<NewState> { NewState.Ignored }
            }

        val buyDubLinkProActions = actions.ofType(Action.BuyDubLinkPro::class.java)
            .switchMapCompletable { action ->
                useCase.buyDubLinkPro(action.activity)
                    .subscribeOn(rxScheduler.io)
                    .observeOn(rxScheduler.ui)
            }

        val allActions = Observable.merge(
            listOf(
                observePurchaseUpdatesActions,
                getSkuDetailsActions,
                queryPurchasesActions,
                queryPurchaseHistoryActions
            )
        )

        val fireAndForgetActions = Completable.merge(
            listOf(
                buyDubLinkProActions
            )
        )

        disposables += allActions
            .scan(initialState, reducer)
            .distinctUntilChanged()
            .subscribe(state::postValue, Timber::e)

        disposables += fireAndForgetActions
            .subscribe({ Timber.d("done") }, { Timber.e(it) })
    }
}

sealed class NewState {
    data class SkuDetails(val skuDetailsResponse: SkuDetailsResponse) : NewState()
    data class Purchases(val purchases: List<Purchase>) : NewState()
    data class PurchaseUpdate(val purchasesUpdate: PurchasesUpdate) : NewState()
    object Ignored : NewState()
}

sealed class Action : BaseAction {
    object ObservePurchaseUpdates : Action()
    object QuerySkuDetails : Action()
    object QueryPurchases : Action()
    object QueryPurchaseHistory : Action()
    data class BuyDubLinkPro(val activity: Activity) : Action()
}

data class State(
    val dubLinkProPrice: String?,
    val canPurchaseDubLinkPro: Boolean?,
    val errorMessage: String?
) : BaseState

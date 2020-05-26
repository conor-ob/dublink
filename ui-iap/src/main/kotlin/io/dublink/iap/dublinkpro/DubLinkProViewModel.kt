package io.dublink.iap.dublinkpro

import android.app.Activity
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import io.dublink.domain.service.RxScheduler
import io.dublink.iap.BillingException
import io.dublink.iap.DubLinkSku
import io.dublink.iap.PurchasesUpdate
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
        dubLinkProPurchased = null,
        message = null
    )

    private val reducer: Reducer<State, NewState> = { state, newState ->
        when (newState) {
            is NewState.SkuDetail -> {
                val dubLinkProSkuDetails = newState.skuDetails.find { DubLinkSku.DUBLINK_PRO.productId == it.sku }
                if (dubLinkProSkuDetails != null) {
                    State(
                        dubLinkProPrice = "€0 (TEST)", // TODO add actual price
                        message = null,
                        dubLinkProPurchased = null,
                        canPurchaseDubLinkPro = state.canPurchaseDubLinkPro
                    )
                } else {
                    State(
                        dubLinkProPrice = state.dubLinkProPrice,
                        message = null,
                        dubLinkProPurchased = null,
                        canPurchaseDubLinkPro = state.canPurchaseDubLinkPro
                    )
                }
            }
            is NewState.Purchases -> {
                val dubLinkProPurchase = newState.purchases.find { DubLinkSku.DUBLINK_PRO.productId == it.sku }
                State(
                    canPurchaseDubLinkPro = dubLinkProPurchase == null,
                    dubLinkProPrice = state.dubLinkProPrice,
                    dubLinkProPurchased = null,
                    message = null
                )
            }
            is NewState.PurchaseUpdate -> State(
                canPurchaseDubLinkPro = newState.purchasesUpdate is PurchasesUpdate.Canceled,
                dubLinkProPrice = state.dubLinkProPrice,
                dubLinkProPurchased = newState.purchasesUpdate is PurchasesUpdate.Success &&
                    newState.purchasesUpdate.purchases.map { it.sku }.contains(DubLinkSku.DUBLINK_PRO.productId),
                message = when (newState.purchasesUpdate) {
                    is PurchasesUpdate.Success -> null
                    is PurchasesUpdate.Canceled -> null // "Purchase Cancelled"
                    is PurchasesUpdate.Failed -> "Something went wrong, try refreshing"
                }
            )
            is NewState.Error -> State(
                canPurchaseDubLinkPro = state.canPurchaseDubLinkPro,
                dubLinkProPrice = state.dubLinkProPrice,
                dubLinkProPurchased = null,
                message = newState.message
            )
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
                    .map<NewState> { purchasesUpdate -> NewState.PurchaseUpdate(purchasesUpdate) }
                    .onErrorReturn { throwable -> newStateFromThrowable(throwable) }
                    .toObservable()
            }

        val getSkuDetailsActions = actions.ofType(Action.QuerySkuDetails::class.java)
            .switchMap {
                useCase.getSkuDetails()
                    .subscribeOn(rxScheduler.io)
                    .observeOn(rxScheduler.ui)
                    .map<NewState> { skuDetails -> NewState.SkuDetail(skuDetails) }
                    .onErrorReturn { throwable -> newStateFromThrowable(throwable) }
                    .toObservable()
            }

        val queryPurchasesActions = actions.ofType(Action.QueryPurchases::class.java)
            .switchMap {
                useCase.getPurchases()
                    .subscribeOn(rxScheduler.io)
                    .observeOn(rxScheduler.ui)
                    .map<NewState> { purchases -> NewState.Purchases(purchases) }
                    .onErrorReturn { throwable -> newStateFromThrowable(throwable) }
                    .toObservable()
            }

        val buyDubLinkProActions = actions.ofType(Action.BuyDubLinkPro::class.java)
            .switchMap { action ->
                useCase.buyDubLinkPro(action.activity)
                    .subscribeOn(rxScheduler.io)
                    .observeOn(rxScheduler.ui)
                    .toSingleDefault<NewState>(NewState.Ignored)
                    .onErrorReturn { throwable -> newStateFromThrowable(throwable) }
                    .toObservable()
            }

        val allActions = Observable.merge(
            listOf(
                observePurchaseUpdatesActions,
                getSkuDetailsActions,
                queryPurchasesActions,
                buyDubLinkProActions
            )
        )

        disposables += allActions
            .scan(initialState, reducer)
            .distinctUntilChanged()
            .subscribe(state::postValue, Timber::e)
    }

    private fun newStateFromThrowable(throwable: Throwable): NewState {
        val message = when (throwable) {
            is BillingException.ServiceDisconnectedException,
            is BillingException.ServiceUnavailableException -> "In App Purchases are unavailable at the moment"
            is BillingException.UserCanceledException -> "Purchase Cancelled"
            else -> "Something went wrong, try refreshing"
        }
        return NewState.Error(message)
    }
}

sealed class NewState {
    data class SkuDetail(val skuDetails: List<SkuDetails>) : NewState()
    data class Purchases(val purchases: List<Purchase>) : NewState()
    data class PurchaseUpdate(val purchasesUpdate: PurchasesUpdate) : NewState()
    data class Error(val message: String) : NewState()
    object Ignored : NewState()
}

sealed class Action : BaseAction {
    object ObservePurchaseUpdates : Action()
    object QuerySkuDetails : Action()
    object QueryPurchases : Action()
    data class BuyDubLinkPro(val activity: Activity) : Action()
}

data class State(
    val dubLinkProPrice: String?,
    val canPurchaseDubLinkPro: Boolean?,
    val dubLinkProPurchased: Boolean?,
    val message: String?
) : BaseState

package io.dublink.iap.dublinkpro

import android.app.Activity
import com.android.billingclient.api.Purchase
import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import io.dublink.iap.PurchasesUpdate
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject
import timber.log.Timber

class DubLinkProViewModel @Inject constructor(
    private val useCase: DubLinkProUseCase
) : BaseViewModel<Action, State>() {

    override val initialState = State(
        dubLinkProPrice = null,
        errorMessage = null
    )

    private val reducer: Reducer<State, NewState> = { state, newState ->
        when (newState) {
            is NewState.SkuDetails -> {
                when (newState.skuDetailsResponse) {
                    is SkuDetailsResponse.Data -> State(
                        dubLinkProPrice = newState.skuDetailsResponse.skuDetails.price,
                        errorMessage = null
                    )
                    is SkuDetailsResponse.Error -> State(
                        dubLinkProPrice = state.dubLinkProPrice,
                        errorMessage = newState.skuDetailsResponse.message
                    )
                }
            }
            is NewState.Purchases -> state
            is NewState.PurchaseUpdate -> {
                Timber.d(newState.purchasesUpdate.toString())
                state
            }
            is NewState.DubLinkProPurchased -> state
        }
    }

    init {
        bindActions()
    }

    private fun bindActions() {
        val observePurchaseUpdatesActions = actions.ofType(Action.ObservePurchaseUpdates::class.java)
            .switchMap {
                useCase.observePurchaseUpdates().toObservable()
                    .map<NewState> { NewState.PurchaseUpdate(it) }
            }

        val getSkuDetailsActions = actions.ofType(Action.QuerySkuDetails::class.java)
            .switchMapSingle {
                useCase.getDubLinkProSkuDetails()
//                    .subscribeOn(rxScheduler.io) // TODO does it need to be ui thread?
//                    .observeOn(rxScheduler.ui)
                    .map<NewState> { response -> NewState.SkuDetails(response) }
            }

        val queryPurchasesActions = actions.ofType(Action.QueryPurchases::class.java)
            .switchMapSingle {
                useCase.getPurchases()
                    .map<NewState> { NewState.Purchases(it) }
            }

        val buyDubLinkProActions = actions.ofType(Action.BuyDubLinkPro::class.java)
            .switchMap { action ->
                useCase.buyDubLinkPro(action.activity)
                    .map<NewState> { NewState.DubLinkProPurchased() }
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
}

sealed class NewState {
    data class SkuDetails(val skuDetailsResponse: SkuDetailsResponse) : NewState()
    data class Purchases(val purchases: List<Purchase>) : NewState()
    data class PurchaseUpdate(val purchasesUpdate: PurchasesUpdate) : NewState()
    data class DubLinkProPurchased(val message: String = "wOO") : NewState()
}

sealed class Action : BaseAction {
    object ObservePurchaseUpdates : Action()
    object QuerySkuDetails : Action()
    object QueryPurchases : Action()
    data class BuyDubLinkPro(val activity: Activity) : Action()
}

data class State(
    val dubLinkProPrice: String? = null,
    val canPurchaseDubLinkPro: Boolean? = null,
    val errorMessage: String? = null
) : BaseState

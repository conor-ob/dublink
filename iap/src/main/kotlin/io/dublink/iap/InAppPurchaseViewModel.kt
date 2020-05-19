package io.dublink.iap

import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject

class InAppPurchaseViewModel @Inject constructor(
    private val reactiveBillingClient: ReactiveBillingClient
) : BaseViewModel<Action, State>() {

    override val initialState = State(
        dubLinkProPrice = null
    )

    private val reducer: Reducer<State, NewState> = { state, newState ->
        when (newState) {
            is NewState.SkuDetails -> {
                val p = getPrice(newState.skuDetails)
                Timber.d(p)
                State(
                    dubLinkProPrice = p
                )
            }
            is NewState.Purchases -> State(
                dubLinkProPrice = state.dubLinkProPrice
            )
        }
    }

    private fun getPrice(skuDetails: SkuDetailsResponse): String? {
        return if (skuDetails is SkuDetailsResponse.Data) {
            skuDetails.skuDetails.find { it.sku == InAppPurchaseRepository.DubLinkSku.DUBLINK_PRO.productId }?.price
        } else {
            null
        }
    }

    init {
        bindActions()
    }

//    override fun onCleared() {
//        super.onCleared()
//        reactiveBillingClient.disconnect()
//    }

    private fun bindActions() {
//        val connectActions = actions.ofType(Action.Connect::class.java)
//            .switchMap {
//                reactiveBillingClient.connect()
//                    .map<NewState> { response -> NewState.SkuDetails(SkuDetailsResponse.Data(emptyList())) }
//            }

        val querySkuDetailsActions = actions.ofType(Action.QuerySkuDetails::class.java)
            .switchMapSingle {
                Timber.d("${object{}.javaClass.enclosingMethod?.name}")
                reactiveBillingClient.getSkuDetails()
                    .map<NewState> {
                            response -> NewState.SkuDetails(response) }
            }

//        val queryPurchasesActions = actions.ofType(Action.QueryPurchases::class.java)
//            .switchMap {
//                reactiveBillingClient.getPurchases()
//                    .map<NewState> { response -> NewState.Purchases(response) }
//            }

        val allActions = Observable.merge(
            listOf(
//                connectActions
                querySkuDetailsActions
//                queryPurchasesActions
            )
        )

        disposables += allActions
            .scan(initialState, reducer)
            .distinctUntilChanged()
            .subscribe(state::postValue, Timber::e)
    }

    fun start() {
        bindActions()
    }

    fun stop() {
        disposables.clear()
        disposables.dispose()
    }
}

sealed class NewState {

    data class SkuDetails(val skuDetails: SkuDetailsResponse) : NewState()
    data class Purchases(val skuDetails: PurchasesResponse) : NewState()
}

sealed class Action : BaseAction {
    object Connect : Action()
    object QuerySkuDetails : Action()
    object QueryPurchases : Action()
}

data class State(
    val dubLinkProPrice: String? = null
) : BaseState

package io.dublink.iap.dublinkpro

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetailsParams
import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import io.dublink.iap.RxBilling
import io.dublink.iap.SkuDetailsResponse
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject
import timber.log.Timber

class DubLinkProViewModel @Inject constructor(
    private val rxBilling: RxBilling
//    private val reactiveBillingClient: ReactiveBillingClient,
//    private val rxScheduler: RxScheduler
) : BaseViewModel<Action, State>() {

    override val initialState = State(
        dubLinkProPrice = null
    )

    private val reducer: Reducer<State, NewState> = { state, newState ->
        when (newState) {
            is NewState.SkuDetails -> State(
                dubLinkProPrice = when (newState.response) {
                    is SkuDetailsResponse.Data -> newState.response.skuDetails.firstOrNull()?.price
                    is SkuDetailsResponse.Error -> null
                }
            )
            is NewState.Purchases -> state
        }
    }

    init {
        bindActions()
    }

    private fun bindActions() {
        val getSkuDetailsActions = actions.ofType(Action.QuerySkuDetails::class.java)
            .switchMapSingle {
                rxBilling.getSkuDetails(
                    SkuDetailsParams.newBuilder()
                    .setSkusList(listOf("android.test.purchased"))
                    .setType(BillingClient.SkuType.INAPP)
                    .build())
//                    .subscribeOn(rxScheduler.io) // TODO does it need to be ui thread?
//                    .observeOn(rxScheduler.ui)
                    .map<NewState> { response ->
                        NewState.SkuDetails(
                            SkuDetailsResponse.Data(response)
                        )
                    }
            }

        val queryPurchasesActions = actions.ofType(Action.QueryPurchases::class.java)
            .switchMapSingle {
                rxBilling.getPurchases(BillingClient.SkuType.INAPP)
                    .map<NewState> { NewState.Purchases(it) }
            }

        val allActions = Observable.merge(
            listOf(
                getSkuDetailsActions,
                queryPurchasesActions
            )
        )

        disposables += allActions
            .scan(initialState, reducer)
            .distinctUntilChanged()
            .subscribe(state::postValue, Timber::e)
    }
}

sealed class NewState {
    data class SkuDetails(val response: SkuDetailsResponse) : NewState()
    data class Purchases(val purchases: List<Purchase>) : NewState()
}

sealed class Action : BaseAction {
    object QuerySkuDetails : Action()
    object QueryPurchases : Action()
}

data class State(
    val dubLinkProPrice: String? = null
) : BaseState

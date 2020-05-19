package io.dublink.iap

import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import io.dublink.domain.service.RxScheduler
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.SingleSource
import io.reactivex.rxkotlin.flatMapSequence
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject

class InAppPurchaseViewModel @Inject constructor(
    private val reactiveBillingClient: ReactiveBillingClient,
    private val rxScheduler: RxScheduler
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
        }
    }

    init {
        bindActions()
    }

    private fun bindActions() {
        val getSkuDetailsActions = actions.ofType(Action.QuerySkuDetails::class.java)
            .switchMapSingle {
                reactiveBillingClient.getSkuDetails()
//                    .subscribeOn(rxScheduler.io) // TODO does it need to be ui thread?
//                    .observeOn(rxScheduler.ui)
                    .map<NewState> { response -> NewState.SkuDetails(response) }
            }

        val allActions = Observable.merge(
            listOf(
                getSkuDetailsActions
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
}

sealed class Action : BaseAction {
    object QuerySkuDetails : Action()
}

data class State(
    val dubLinkProPrice: String? = null
) : BaseState

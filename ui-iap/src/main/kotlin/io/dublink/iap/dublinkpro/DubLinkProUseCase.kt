package io.dublink.iap.dublinkpro

import android.app.Activity
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchaseHistoryRecord
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import io.dublink.domain.service.PreferenceStore
import io.dublink.iap.DubLinkSku
import io.dublink.iap.PurchasesUpdate
import io.dublink.iap.RxBilling
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class DubLinkProUseCase @Inject constructor(
    private val rxBilling: RxBilling,
    private val preferenceStore: PreferenceStore
) {

    private val skuDetailsCache = mutableMapOf<DubLinkSku, SkuDetails>()

    fun observePurchaseUpdates(): Flowable<PurchasesUpdate> {
        return rxBilling.observeUpdates().doOnNext {
            if (it is PurchasesUpdate.Success) {
                processPurchases(it.purchases)
            }
        }
    }

    fun getDubLinkProSkuDetails(): Single<SkuDetailsResponse> {
        return rxBilling.getSkuDetails(
            SkuDetailsParams.newBuilder()
                .setSkusList(listOf(DubLinkSku.DUBLINK_PRO.productId))
                .setType(BillingClient.SkuType.INAPP)
                .build()
        ).map { skuDetails ->
            val result = skuDetails.find { it.sku == DubLinkSku.DUBLINK_PRO.productId }
            if (result != null) {
                skuDetailsCache[DubLinkSku.DUBLINK_PRO] = result
                return@map SkuDetailsResponse.Data(result)
            }
            return@map SkuDetailsResponse.Error("404")
        }.onErrorReturn {
            SkuDetailsResponse.Error(it.message ?: it.cause?.message ?: "")
        }
    }

    fun getPurchases(): Single<List<Purchase>> {
        return rxBilling.getPurchases(BillingClient.SkuType.INAPP).doOnSuccess {
            processPurchases(it.orEmpty())
        }
    }

    fun getPurchaseHistory(): Single<List<PurchaseHistoryRecord>> {
        return rxBilling.getPurchaseHistory(BillingClient.SkuType.INAPP).doOnSuccess {
            Timber.d("CONOR getPurchaseHistory")
            Timber.d("CONOR $it")
        }
    }

    fun buyDubLinkPro(activity: Activity): Completable {
        return rxBilling.launchFlow(
            activity,
            BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetailsCache.getValue(DubLinkSku.DUBLINK_PRO))
                .build()
        )
    }

    private fun processPurchases(purchases: List<Purchase>) {
        for (purchase in purchases) {
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                if (isSignatureValid(purchase)) {
                    acknowledgePurchase(purchase)
//                    consumePurchase(purchase)
                }
            }
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        rxBilling.acknowledge(
            AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
        )
            .doOnComplete {
                preferenceStore.setDubLinkProEnabled(true)
            }
            .doOnError {

            }
            .subscribe()
    }

    private fun consumePurchase(purchase: Purchase) {
        rxBilling.consumeProduct(
            ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
        )
            .doOnComplete {
                preferenceStore.setDubLinkProEnabled(false)
            }
            .subscribe()
    }

    private fun isSignatureValid(purchase: Purchase): Boolean {
        return true
    }
}

sealed class SkuDetailsResponse {

    data class Data(val skuDetails: SkuDetails) : SkuDetailsResponse()
    data class Error(val message: String) : SkuDetailsResponse()
}

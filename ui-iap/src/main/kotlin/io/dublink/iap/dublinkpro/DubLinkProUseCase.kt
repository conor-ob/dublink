package io.dublink.iap.dublinkpro

import android.app.Activity
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import io.dublink.domain.service.PreferenceStore
import io.dublink.iap.DubLinkSku
import io.dublink.iap.PurchasesUpdate
import io.dublink.iap.RxBilling
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject
import timber.log.Timber

class DubLinkProUseCase @Inject constructor(
    private val rxBilling: RxBilling,
    private val preferenceStore: PreferenceStore
) {

    private val skuDetailsCache = mutableMapOf<DubLinkSku, SkuDetails>()

    fun observePurchaseUpdates(): Flowable<PurchasesUpdate> {
        return rxBilling.observeUpdates().flatMapSingle { update ->
            if (update.purchases.isNullOrEmpty()) {
                Single.just(update)
            } else {
                Single.zip(
                    update.purchases.map { purchase ->
                        processPurchase(purchase)
                    }
                ) { update }
            }
        }
    }

    fun getSkuDetails(): Single<List<SkuDetails>> {
        return rxBilling.getSkuDetails(
            SkuDetailsParams.newBuilder()
                .setSkusList(listOf(DubLinkSku.DUBLINK_PRO.productId))
                .setType(BillingClient.SkuType.INAPP)
                .build()
        ).map { skuDetails ->
            val result = skuDetails.find { it.sku == DubLinkSku.DUBLINK_PRO.productId }
            if (result != null) {
                Timber.d("Found DubLink Pro sku details")
                Timber.d(result.toString())
                skuDetailsCache[DubLinkSku.DUBLINK_PRO] = result
            }
            skuDetails
        }
    }

    fun getPurchases(): Single<List<Purchase>> {
        return rxBilling.getPurchases(
            BillingClient.SkuType.INAPP
        ).flatMap { purchases ->
            if (purchases.isNullOrEmpty()) {
                Timber.d("No purchases on record - removing DubLink Pro access")
                preferenceStore.setDubLinkProEnabled(false)
                Single.just(emptyList())
            } else {
                Single.zip(
                    purchases.map { purchase ->
                        processPurchase(purchase)
                    }
                ) { purchase -> purchase.map { it as Purchase }.toList() }
            }
        }
    }

    private fun processPurchase(purchase: Purchase): Single<Purchase> {
        Timber.d("Processing purchase")
        Timber.d(purchase.toString())
        return if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && isSignatureValid(purchase)) {
            acknowledgePurchase(purchase)
//            consumePurchase(purchase)
        } else {
            retractPurchase(purchase)
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

    private fun acknowledgePurchase(purchase: Purchase): Single<Purchase> {
        return if (DubLinkSku.DUBLINK_PRO.productId == purchase.sku) {
            if (purchase.isAcknowledged) {
                Timber.d("Found previously acknowledged DubLink Pro purchase - granting DubLink Pro access")
                Timber.d(purchase.toString())
                preferenceStore.setDubLinkProEnabled(true)
                Single.just(purchase)
            } else {
                Timber.d("Acknowledging DubLink Pro purchase")
                Timber.d(purchase.toString())
                rxBilling.acknowledge(
                    AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                )
                    .doOnComplete {
                        Timber.d("DubLink Pro purchase acknowledged - granting DubLink Pro access")
                        Timber.d(purchase.toString())
                        preferenceStore.setDubLinkProEnabled(true)
                    }
                    .toSingle { purchase }
            }
        } else {
            Single.just(purchase)
        }
    }

    private fun retractPurchase(purchase: Purchase): Single<Purchase> {
        if (DubLinkSku.DUBLINK_PRO.productId == purchase.sku) {
            preferenceStore.setDubLinkProEnabled(false)
        }
        return Single.just(purchase)
    }

    private fun consumePurchase(purchase: Purchase): Single<Purchase> {
        return rxBilling.consumeProduct(
            ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
        )
            .doOnComplete {
                preferenceStore.setDubLinkProEnabled(true)
            }
            .toSingle { purchase }
    }

    private fun isSignatureValid(purchase: Purchase): Boolean {
        return true
    }
}

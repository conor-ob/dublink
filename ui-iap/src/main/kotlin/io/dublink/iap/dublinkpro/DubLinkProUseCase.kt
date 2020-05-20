package io.dublink.iap.dublinkpro

import android.app.Activity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import io.dublink.domain.service.PreferenceStore
import io.dublink.iap.DubLinkSku
import io.dublink.iap.PurchasesUpdate
import io.dublink.iap.RxBilling
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class DubLinkProUseCase @Inject constructor(
    private val rxBilling: RxBilling,
    private val preferenceStore: PreferenceStore
) {

    private val skuDetailsCache = mutableMapOf<DubLinkSku, SkuDetails>()

    fun observePurchaseUpdates(): Flowable<PurchasesUpdate> {
        return rxBilling.observeUpdates()
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
        return rxBilling.getPurchases(BillingClient.SkuType.INAPP)
    }

    fun buyDubLinkPro(activity: Activity): Observable<Boolean> {
        return rxBilling.launchFlow(
            activity,
            BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetailsCache.getValue(DubLinkSku.DUBLINK_PRO))
                .build()
        ).toObservable()
    }
}

sealed class SkuDetailsResponse {

    data class Data(val skuDetails: SkuDetails) : SkuDetailsResponse()
    data class Error(val message: String) : SkuDetailsResponse()
}
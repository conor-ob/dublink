package io.dublink.iap

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.gen.rxbilling.client.RxBilling
import com.gen.rxbilling.client.RxBillingImpl
import com.gen.rxbilling.connection.BillingClientFactory
import io.dublink.domain.service.PreferenceStore
import io.dublink.iap.InAppPurchaseRepository.DubLinkSku.IN_APP_SKUS
import io.dublink.iap.InAppPurchaseRepository.DubLinkSku.TEST
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

class InAppPurchaseRepository @Inject constructor(
    private val context: Context,
    private val preferenceStore: PreferenceStore
) {

    private lateinit var rxBilling: RxBilling
    private val disposable = CompositeDisposable()

    fun startBillingDataSourceConnections(activity: Activity) {
        rxBilling = RxBillingImpl(BillingClientFactory(context.applicationContext))
        disposable.add(
            rxBilling.observeUpdates()
                .subscribe({
                    Timber.d("observeUpdates $it")
                }, {
                    Timber.e(it)
                })
        )

        disposable.add(
            rxBilling.getSkuDetails(
                SkuDetailsParams.newBuilder().setSkusList(IN_APP_SKUS).setType(BillingClient.SkuType.INAPP).build()
            )
                .subscribe({
                    Timber.d("skudeets $it")
                }, {
                    Timber.e(it)
                })
        )



//        disposable.add(
//            rxBilling.launchFlow(activity, BillingFlowParams.newBuilder()
//                .setSkuDetails(SkuDetails("{$TEST}"))
//                .build())
//                .subscribe({
//                    Timber.d("startFlowWithClient")
//                }, {
//                    Timber.e(it)
//                }))
    }

    private object DubLinkSku {
        const val TEST = "android.test.purchased"
        const val DUBLINK_PRO = "dublink_pro"
        val IN_APP_SKUS = listOf(TEST, DUBLINK_PRO)
    }
}

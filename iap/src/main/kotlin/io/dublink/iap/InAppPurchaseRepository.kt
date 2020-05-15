package io.dublink.iap

import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetailsParams
import com.github.rholder.retry.RetryerBuilder
import com.github.rholder.retry.StopStrategies
import com.github.rholder.retry.WaitStrategies
import io.dublink.domain.service.PreferenceStore
import timber.log.Timber
import java.time.LocalTime
import java.util.HashSet
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class InAppPurchaseRepository @Inject constructor(
    private val context: Context,
    private val preferenceStore: PreferenceStore
) {

    private lateinit var billingClient: BillingClient
    private var isRunning = false

    fun startBillingDataSourceConnections() {
        isRunning = false
        billingClient = BillingClient
        .newBuilder(context)
        .enablePendingPurchases()
        .setListener(purchasesUpdatedListener)
        .build()

        val retryPolicy = RetryerBuilder.newBuilder<Boolean>()
            .retryIfResult { it == true }
            .withWaitStrategy(WaitStrategies.exponentialWait(100, 5L, TimeUnit.MINUTES))
            .withStopStrategy(StopStrategies.neverStop())
            .build()

        retryPolicy.call {
            Timber.d("Trying at ${LocalTime.now()}")
            if (!billingClient.isReady) {
                billingClient.startConnection(
                    object : BillingClientStateListener {

                        override fun onBillingSetupFinished(billingResult: BillingResult) {
                            when (billingResult.responseCode) {
                                BillingClient.BillingResponseCode.OK -> {
                                    isRunning = true
                                    Timber.d("onBillingSetupFinished successfully")
                                    querySkuDetailsAsync(BillingClient.SkuType.INAPP, DubLinkSku.IN_APP_SKUS)
                                    queryPurchasesAsync()
                                }
                                BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {
                                    //Some apps may choose to make decisions based on this knowledge.
                                    Timber.d(billingResult.debugMessage)
                                }
                                else -> {
                                    //do nothing. Someone else will connect it through retry policy.
                                    //May choose to send to server though
                                    Timber.d(billingResult.debugMessage)
                                }
                            }
                        }

                        override fun onBillingServiceDisconnected() {
                            startBillingDataSourceConnections()
                        }
                    }
                )
            }
            return@call billingClient.isReady
        }
    }

    fun endBillingDataSourceConnections() {
        billingClient.endConnection()
        isRunning = false
    }

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        Timber.d(billingResult.toString())
        Timber.d(purchases.toString())
    }

    private fun querySkuDetailsAsync(skuType: String, skuList: List<String>) {
        val params = SkuDetailsParams.newBuilder()
            .setSkusList(skuList)
            .setType(skuType)
            .build()
        Timber.d("querySkuDetailsAsync for $skuType")
        billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
            when (billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    Timber.d(skuDetailsList.toString())
//                    if (skuDetailsList.orEmpty().isNotEmpty()) {
//                        skuDetailsList.forEach {
//                            CoroutineScope(Job() + Dispatchers.IO).launch {
//                                localCacheBillingClient.skuDetailsDao().insertOrUpdate(it)
//                            }
//                        }
//                    }
                }
                else -> {
                    Timber.e(billingResult.debugMessage)
                }
            }
        }
    }

    private fun queryPurchasesAsync() {
        Timber.d("queryPurchasesAsync called")
        val purchasesResult = HashSet<Purchase>()
        val result = billingClient.queryPurchases(BillingClient.SkuType.INAPP)
        Timber.d("queryPurchasesAsync INAPP results: ${result.purchasesList?.size}")
        result.purchasesList?.apply { purchasesResult.addAll(this) }
        processPurchases(purchasesResult)
    }

    private fun processPurchases(purchasesResult: Set<Purchase>) {
        Timber.d(purchasesResult.toString())
    }

    private object DubLinkSku {
        const val TEST = "android.test.purchased"
        const val DUBLINK_PRO = "dublink_pro"
        val IN_APP_SKUS = listOf(TEST, DUBLINK_PRO)
    }
}

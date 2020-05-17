package io.dublink.iap

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import io.dublink.domain.service.PreferenceStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.HashSet
import javax.inject.Inject

class InAppPurchaseRepository @Inject constructor(
    private val context: Context,
    private val preferenceStore: PreferenceStore
) : BillingClientStateListener, PurchasesUpdatedListener {

    val dubLinkProPriceLiveData = MutableLiveData<String>()

    private lateinit var playStoreBillingClient: BillingClient
    private val skuDetailsCache = mutableMapOf<DubLinkSku, SkuDetails>()
    private val purchasesCache = mutableMapOf<DubLinkSku, Purchase>()

    fun startDataSourceConnections() {
        Timber.d("startDataSourceConnections")
        instantiateAndConnectToPlayBillingService()
    }

    private fun instantiateAndConnectToPlayBillingService() {
        playStoreBillingClient = BillingClient.newBuilder(context.applicationContext)
            .enablePendingPurchases() // required or app will crash
            .setListener(this)
            .build()
        connectToPlayBillingService()
    }

    private fun connectToPlayBillingService(): Boolean {
        if (!playStoreBillingClient.isReady) {
            playStoreBillingClient.startConnection(this)
            return true
        }
        return false
    }

    fun endDataSourceConnections() {
        playStoreBillingClient.endConnection()
        // normally you don't worry about closing a DB connection unless you have more than
        // one DB open. so no need to call 'localCacheBillingClient.close()'
        Timber.d("endDataSourceConnections")
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                Timber.d("onBillingSetupFinished successfully")
                querySkuDetailsAsync(BillingClient.SkuType.INAPP, DubLinkSku.inAppSkus)
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
        Timber.d("onBillingServiceDisconnected")
        connectToPlayBillingService()
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                // will handle server verification, consumables, and updating the local cache
                purchases?.apply { processPurchases(this.toSet()) }
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                // item already owned? call queryPurchasesAsync to verify and process all such items
                Timber.d(billingResult.debugMessage)
                queryPurchasesAsync()
            }
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> {
                connectToPlayBillingService()
            }
            else -> {
                Timber.i(billingResult.debugMessage)
            }
        }
    }

    fun queryPurchasesAsync() {
        Timber.d("queryPurchasesAsync called")
        val purchasesResult = HashSet<Purchase>()
        var result = playStoreBillingClient.queryPurchases(BillingClient.SkuType.INAPP)
        Timber.d("queryPurchasesAsync INAPP results: ${result?.purchasesList?.size}")
        result?.purchasesList?.apply { purchasesResult.addAll(this) }
//        if (isSubscriptionSupported()) {
//            result = playStoreBillingClient.queryPurchases(BillingClient.SkuType.SUBS)
//            result?.purchasesList?.apply { purchasesResult.addAll(this) }
//            Timber.d("queryPurchasesAsync SUBS results: ${result?.purchasesList?.size}")
//        }
        processPurchases(purchasesResult)
    }

    private fun processPurchases(purchasesResult: Set<Purchase>) =
        CoroutineScope(Job() + Dispatchers.IO).launch {
            Timber.d("processPurchases called")
            val validPurchases = HashSet<Purchase>(purchasesResult.size)
            Timber.d("processPurchases newBatch content $purchasesResult")
            purchasesResult.forEach { purchase ->
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
//                    if (isSignatureValid(purchase)) { // TODO
                        validPurchases.add(purchase)
//                    }
                } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
                    Timber.d("Received a pending purchase of SKU: ${purchase.sku}")
                    // handle pending purchases, e.g. confirm with users about the pending
                    // purchases, prompt them to complete it, etc.
                }
            }
            val (consumables, nonConsumables) = validPurchases.partition {
                DubLinkSku.consumableSkus.contains(it.sku)
            }
            Timber.d("processPurchases consumables content $consumables")
            Timber.d("processPurchases non-consumables content $nonConsumables")
            /*
              As is being done in this sample, for extra reliability you may store the
              receipts/purchases to a your own remote/local database for until after you
              disburse entitlements. That way if the Google Play Billing library fails at any
              given point, you can independently verify whether entitlements were accurately
              disbursed. In this sample, the receipts are then removed upon entitlement
              disbursement.
             */
//            val testing = localCacheBillingClient.purchaseDao().getPurchases()
//            Timber.d("processPurchases purchases in the lcl db ${testing?.size}")
            validPurchases.forEach { validPurchase ->
                val dubLinkSku = DubLinkSku.values()
                    .find { dubLinkSku -> dubLinkSku.productId == validPurchase.sku }
                if (dubLinkSku != null) {
                    purchasesCache[dubLinkSku] = validPurchase
                }
            }
//            localCacheBillingClient.purchaseDao().insert(*validPurchases.toTypedArray())


            // switch these around to reset
//            acknowledgeNonConsumablePurchasesAsync(consumables)
            handleConsumablePurchasesAsync(consumables)
        }

    private fun acknowledgeNonConsumablePurchasesAsync(nonConsumables: List<Purchase>) {
        nonConsumables.forEach { purchase ->
            val params = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase
                .purchaseToken).build()
            playStoreBillingClient.acknowledgePurchase(params) { billingResult ->
                when (billingResult.responseCode) {
                    BillingClient.BillingResponseCode.OK -> {
                        disburseNonConsumableEntitlement(purchase)
                    }
                    else -> Timber.d("acknowledgeNonConsumablePurchasesAsync response is ${billingResult.debugMessage}")
                }
            }
        }
    }

    private fun disburseNonConsumableEntitlement(purchase: Purchase) =
        CoroutineScope(Job() + Dispatchers.IO).launch {
            when (purchase.sku) {
                DubLinkSku.DUBLINK_PRO.productId -> {
                    preferenceStore.setDubLinkProEnabled(true) // TODO when to set to false?
//                    val premiumCar = PremiumCar(true)
//                    insert(premiumCar)
//                    localCacheBillingClient.skuDetailsDao()
//                        .insertOrUpdate(purchase.sku, premiumCar.mayPurchase())
                }
//                purchasesCache.remove(DubLinkSku.DUBLINK_PRO)
            }
        }

    private fun handleConsumablePurchasesAsync(consumables: List<Purchase>) {
        Timber.d("handleConsumablePurchasesAsync called")
        consumables.forEach {
            Timber.d("handleConsumablePurchasesAsync foreach it is $it")
            val params =
                ConsumeParams.newBuilder().setPurchaseToken(it.purchaseToken).build()
            playStoreBillingClient.consumeAsync(params) { billingResult, purchaseToken ->
                when (billingResult.responseCode) {
                    BillingClient.BillingResponseCode.OK -> {
                        // Update the appropriate tables/databases to grant user the items
//                        purchaseToken.apply { disburseConsumableEntitlements(it) }
                    }
                    else -> {
                        Timber.w(billingResult.debugMessage)
                    }
                }
            }
        }
    }

    private fun querySkuDetailsAsync(
        @BillingClient.SkuType skuType: String,
        skuList: List<String>) {
        val params = SkuDetailsParams.newBuilder().setSkusList(skuList).setType(skuType).build()
        Timber.d("querySkuDetailsAsync for $skuType")
        playStoreBillingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
            when (billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    if (skuDetailsList.orEmpty().isNotEmpty()) {
                        skuDetailsList.forEach { skuDetails ->
                            val dubLinkSku = DubLinkSku.values()
                                .find { dubLinkSku -> dubLinkSku.productId == skuDetails.sku }
                            if (dubLinkSku != null) {
                                if (dubLinkSku == DubLinkSku.DUBLINK_PRO) {
                                    dubLinkProPriceLiveData.value = skuDetails.price
                                }
                                skuDetailsCache[DubLinkSku.DUBLINK_PRO] = skuDetails
                            }
                        }
//                            CoroutineScope(Job() + Dispatchers.IO).launch {
//                                localCacheBillingClient.skuDetailsDao().insertOrUpdate(it) // TODO
//                            }
                    }
                }
                else -> {
                    Timber.e(billingResult.debugMessage)
                }
            }
        }
    }

    fun launchBillingFlow(activity: Activity) {
        val purchaseParams = BillingFlowParams.newBuilder().setSkuDetails(skuDetailsCache[DubLinkSku.DUBLINK_PRO]).build()
        playStoreBillingClient.launchBillingFlow(activity, purchaseParams)
    }

    enum class DubLinkSku(val productId: String, val isConsumable: Boolean) {
        DUBLINK_PRO(productId = "android.test.purchased", isConsumable = true);
//        DUBLINK_PRO_TRIAL(productId = "dublink_pro_trial", isConsumable = false);
//        DUBLINK_PRO(productId = "dublink_pro", isConsumable = false);

        companion object {
            val consumableSkus = values().filter { sku -> sku.isConsumable }.map { sku -> sku.productId }
            val inAppSkus = values().map { sku -> sku.productId }
        }
    }
}

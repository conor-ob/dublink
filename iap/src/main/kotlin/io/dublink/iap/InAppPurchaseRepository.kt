package io.dublink.iap

import android.app.Activity
import android.content.Context
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
        Timber.d("${object{}.javaClass.enclosingMethod?.name}")
        playStoreBillingClient = BillingClient.newBuilder(context.applicationContext)
            .enablePendingPurchases()
            .setListener(this)
            .build()
        connectToPlayBillingService()
    }

    private fun connectToPlayBillingService() {
        Timber.d("${object{}.javaClass.enclosingMethod?.name}")
        if (!playStoreBillingClient.isReady) {
            playStoreBillingClient.startConnection(this)
        }
    }

    fun endDataSourceConnections() {
        Timber.d("${object{}.javaClass.enclosingMethod?.name}")
        playStoreBillingClient.endConnection()
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        Timber.d("${object{}.javaClass.enclosingMethod?.name}")
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                Timber.d("${object{}.javaClass.enclosingMethod?.name} OK")
                // will handle server verification, consumables, and updating the local cache
                purchases?.apply { processPurchases(this.toSet()) }
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                Timber.d("${object{}.javaClass.enclosingMethod?.name} ITEM_ALREADY_OWNED")
                // item already owned? call queryPurchasesAsync to verify and process all such items
                Timber.d(billingResult.debugMessage)
                queryPurchasesAsync()
            }
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> {
                Timber.d("${object{}.javaClass.enclosingMethod?.name} SERVICE_DISCONNECTED")
                connectToPlayBillingService()
            }
            else -> {
                Timber.d("${object{}.javaClass.enclosingMethod?.name} UNKNOWN")
                Timber.d(billingResult.debugMessage)
            }
        }
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        Timber.d("${object{}.javaClass.enclosingMethod?.name}")
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                Timber.d("${object{}.javaClass.enclosingMethod?.name} OK")
                querySkuDetailsAsync()
                queryPurchasesAsync()
            }
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {
                Timber.d("${object{}.javaClass.enclosingMethod?.name} BILLING_UNAVAILABLE")
                // TODO alert dialog
                Timber.d(billingResult.debugMessage)
            }
            else -> {
                Timber.d("${object{}.javaClass.enclosingMethod?.name} UNKNOWN")
                // TODO retry
                Timber.d(billingResult.debugMessage)
            }
        }
    }

    override fun onBillingServiceDisconnected() {
        Timber.d("${object{}.javaClass.enclosingMethod?.name}")
        connectToPlayBillingService()
    }

    private fun querySkuDetailsAsync() {
        Timber.d("${object{}.javaClass.enclosingMethod?.name}")
        val params = SkuDetailsParams.newBuilder()
            .setSkusList(DubLinkSku.inAppSkus)
            .setType(BillingClient.SkuType.INAPP)
            .build()
        playStoreBillingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
            when (billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    Timber.d("${object{}.javaClass.enclosingMethod?.name} OK $skuDetailsList")
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
                    }
                }
                else -> {
                    Timber.d("${object{}.javaClass.enclosingMethod?.name} UNKNOWN")
                    Timber.e(billingResult.debugMessage)
                }
            }
        }
    }

    private fun queryPurchasesAsync() {
        Timber.d("${object{}.javaClass.enclosingMethod?.name}")
        val result = playStoreBillingClient.queryPurchases(BillingClient.SkuType.INAPP)
        Timber.d("${object{}.javaClass.enclosingMethod?.name} ${BillingClient.SkuType.INAPP} results: ${result.purchasesList?.size}")
        val purchasesResult = mutableSetOf<Purchase>()
        result.purchasesList?.apply { purchasesResult.addAll(this) }
        processPurchases(purchasesResult)
    }

    private fun processPurchases(purchasesResult: Set<Purchase>) {
        Timber.d("${object{}.javaClass.enclosingMethod?.name}")
        CoroutineScope(Job() + Dispatchers.IO).launch {
            val validPurchases = HashSet<Purchase>(purchasesResult.size)
            Timber.d("processPurchases new content $purchasesResult")
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
            Timber.d("processPurchases valid content $validPurchases")
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
//            handleConsumablePurchasesAsync(validPurchases.toList())
            acknowledgeNonConsumablePurchasesAsync(validPurchases.toList())
        }
    }

    private fun acknowledgeNonConsumablePurchasesAsync(nonConsumables: List<Purchase>) {
        Timber.d("${object{}.javaClass.enclosingMethod?.name}")
        nonConsumables.forEach { purchase ->
            val params = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase
                .purchaseToken).build()
            playStoreBillingClient.acknowledgePurchase(params) { billingResult ->
                when (billingResult.responseCode) {
                    BillingClient.BillingResponseCode.OK -> {
                        Timber.d("${object{}.javaClass.enclosingMethod?.name} OK")
                        disburseNonConsumableEntitlement(purchase)
                    }
                    BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> {
                        Timber.d("${object{}.javaClass.enclosingMethod?.name} ITEM_NOT_OWNED")
                        retractNonConsumableEntitlement(purchase)
                    }
                    else -> {
                        Timber.d("${object{}.javaClass.enclosingMethod?.name} UNKNOWN")
                    }
                }
            }
        }
    }

    private fun disburseNonConsumableEntitlement(purchase: Purchase) {
        if (DubLinkSku.DUBLINK_PRO.productId == purchase.sku) {
            preferenceStore.setDubLinkProEnabled(true)
        }
    }

    private fun retractNonConsumableEntitlement(purchase: Purchase) {
        if (DubLinkSku.DUBLINK_PRO.productId == purchase.sku) {
            preferenceStore.setDubLinkProEnabled(false)
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
            val inAppSkus = values().map { sku -> sku.productId }
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
}

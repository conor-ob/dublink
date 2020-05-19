package io.dublink.iap

import android.content.Context
import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import io.reactivex.Single
import io.reactivex.SingleEmitter
import java.lang.ref.WeakReference

class ReactiveBillingClient(
    private val context: Context
) {

    private var billingClient: BillingClient? = null

    fun getSkuDetails(): Single<SkuDetailsResponse> {
        return connect()
            .flatMap { billingClient ->
                Single.create<SkuDetailsResponse> { emitter ->
                    val weakReference = WeakReference<SingleEmitter<SkuDetailsResponse>>(emitter)
                    val params = SkuDetailsParams.newBuilder()
                        .setSkusList(listOf("android.test.purchased"))
                        .setType(BillingClient.SkuType.INAPP)
                        .build()

                    billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
                        when (billingResult.responseCode) {
                            BillingClient.BillingResponseCode.OK -> {
                                val observer = weakReference.get()
                                if (observer != null && !observer.isDisposed) {
                                    observer.onSuccess(SkuDetailsResponse.Data(skuDetailsList.orEmpty()))
                                }
                            }
                            else -> {
                                val observer = weakReference.get()
                                if (observer != null && !observer.isDisposed) {
                                    observer.onSuccess(SkuDetailsResponse.Error(billingResult.debugMessage))
                                }
                            }
                        }
                    }
                }
            }
    }

    private fun connect(): Single<BillingClient> = Single.create { emitter ->
        val weakReference = WeakReference<SingleEmitter<BillingClient>>(emitter)
        if (billingClient == null || billingClient?.isReady == false) {
            val client = BillingClient.newBuilder(context.applicationContext)
                .enablePendingPurchases()
                .setListener { _, _ -> Log.d("TAG", "ignored") }
                .build()

            billingClient = client

            client.startConnection(
                object : BillingClientStateListener {

                    override fun onBillingSetupFinished(billingResult: BillingResult) {
                        when (billingResult.responseCode) {
                            BillingClient.BillingResponseCode.OK -> {
                                val observer = weakReference.get()
                                if (observer != null && !observer.isDisposed) {
                                    observer.onSuccess(client)
                                }
                            }
                            else -> {
                                billingClient = null
                            }
                        }
                    }

                    override fun onBillingServiceDisconnected() {
                        billingClient = null
                    }
                }
            )
        } else {
            emitter.onSuccess(requireNotNull(billingClient))
        }
    }
}

sealed class SkuDetailsResponse {

    data class Data(val skuDetails: List<SkuDetails>) : SkuDetailsResponse()

    data class Error(val message: String) : SkuDetailsResponse()
}

package io.dublink.iap

import android.content.Context
import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.android.billingclient.api.SkuDetailsResponseListener
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.lang.ref.WeakReference

class ReactiveBillingClient(
    private val context: Context
) {

    private var billingClient: BillingClient? = null

    fun getSkuDetails(): Single<SkuDetailsResponse> {
        return connect()
            .flatMap { billingClient ->
                Single.create<SkuDetailsResponse> { emitter ->
                    val weakRef = WeakReference<SingleEmitter<SkuDetailsResponse>>(emitter)
                    val params = SkuDetailsParams.newBuilder()
                        .setSkusList(listOf("android.test.purchased"))
                        .setType(BillingClient.SkuType.INAPP)
                        .build()

                    billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
                        when (billingResult.responseCode) {
                            BillingClient.BillingResponseCode.OK -> {
//                                if (emitter.isDisposed) return@querySkuDetailsAsync
                                val observer = weakRef.get()
                                if (observer != null && !observer.isDisposed) {
                                    observer.onSuccess(SkuDetailsResponse.Data(skuDetailsList.orEmpty()))
                                }
                            }
                            else -> {
//                                if (emitter.isDisposed) return@querySkuDetailsAsync
                                val observer = weakRef.get()
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
        val weakRef = WeakReference<SingleEmitter<BillingClient>>(emitter)
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
//                                if (emitter.isDisposed) return
                                val observer = weakRef.get()
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

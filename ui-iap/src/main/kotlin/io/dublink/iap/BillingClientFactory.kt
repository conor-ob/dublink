package io.dublink.iap

import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PurchasesUpdatedListener
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.FlowableTransformer
import java.lang.ref.WeakReference
import timber.log.Timber

class BillingClientFactory(
    private val context: Context,
    private val transformer: FlowableTransformer<BillingClient, BillingClient> = RepeatConnectionTransformer()
) {

    fun createBillingFlowable(listener: PurchasesUpdatedListener): Flowable<BillingClient> {
        val flowable = Flowable.create<BillingClient>({
            val emitter = WeakReference<FlowableEmitter<BillingClient>>(it)
            val billingClient = BillingClient.newBuilder(context)
                    .enablePendingPurchases()
                    .setListener(listener)
                    .build()
            Timber.d("startConnection")
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingServiceDisconnected() {
                    Timber.d("onBillingServiceDisconnected")
                    val observer = emitter.get()
                    if (observer != null && !observer.isCancelled) {
                        observer.onComplete()
                    }
                }

                override fun onBillingSetupFinished(result: BillingResult) {
                    val responseCode = result.responseCode
                    Timber.d("onBillingSetupFinished response $responseCode isReady ${billingClient.isReady}")
                    val observer = emitter.get()
                    if (observer != null && !observer.isCancelled) {
                        if (responseCode == BillingClient.BillingResponseCode.OK) {
                            observer.onNext(billingClient)
                        } else {
                            observer.onError(BillingException.fromResult(result))
                        }
                    } else {
                        if (billingClient.isReady) {
                            billingClient.endConnection() // release resources if there are no observers
                        }
                    }
                }
            })
            // finish connection when no subscribers
            val observer = emitter.get()
            if (observer != null) {
                observer.setCancellable {
                    Timber.d("endConnection")
                    if (billingClient.isReady) {
                        Timber.d("endConnectionReally")
                        billingClient.endConnection()
                    }
                }
            }
        }, BackpressureStrategy.LATEST)

        return flowable.compose(transformer)
    }
}

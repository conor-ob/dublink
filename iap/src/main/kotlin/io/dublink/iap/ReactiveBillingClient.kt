package io.dublink.iap

import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.android.billingclient.api.SkuDetailsResponseListener
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

class ReactiveBillingClient(
    private val context: Context
) {

    private var billingClient: BillingClient? = null

    fun getSkuDetails(): Single<SkuDetailsResponse> {
        return connect()
            .flatMap { billingClient ->
                Timber.d("${object{}.javaClass.enclosingMethod?.name}")
                Single.create<SkuDetailsResponse> { emitter ->
                    Timber.d("${object{}.javaClass.enclosingMethod?.name}")
                    if (emitter.isDisposed) {
                        return@create
                    }

                    val params = SkuDetailsParams.newBuilder()
                        .setSkusList(InAppPurchaseRepository.DubLinkSku.inAppSkus)
                        .setType(BillingClient.SkuType.INAPP)
                        .build()

                    billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
                        when (billingResult.responseCode) {
                            BillingClient.BillingResponseCode.OK -> {
                                Timber.d(skuDetailsList.toString())
                                emitter.onSuccess(SkuDetailsResponse.Data(skuDetailsList.orEmpty()))
                            }
                            else -> {
                                emitter.onSuccess(SkuDetailsResponse.Error(billingResult.responseCode))
                            }
                        }
                    }
                }
            }
    }



//    fun getSkuDetails(): Observable<SkuDetailsResponse> {
//        return connect()
//            .flatMapObservable { billingClient ->
//                Observable.create<SkuDetailsResponse> { emitter ->
//                    val params = SkuDetailsParams.newBuilder()
//                        .setSkusList(InAppPurchaseRepository.DubLinkSku.inAppSkus)
//                        .setType(BillingClient.SkuType.INAPP)
//                        .build()
//
//                    billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
//                        when (billingResult.responseCode) {
//                            BillingClient.BillingResponseCode.OK -> {
//                                emitter.onNext(SkuDetailsResponse.Data(skuDetailsList.orEmpty()))
//                                emitter.onComplete()
//                            }
//                            else -> {
//                                emitter.onNext(SkuDetailsResponse.Error(billingResult.responseCode))
//                                emitter.onComplete()
//                            }
//                        }
//                    }
//                }
//            }.subscribeOn(Schedulers.io())
//    }
//                Observable.create<SkuDetailsResponse> { emitter ->
//                    val params = SkuDetailsParams.newBuilder()
//                        .setSkusList(InAppPurchaseRepository.DubLinkSku.inAppSkus)
//                        .setType(BillingClient.SkuType.INAPP)
//                        .build()
//                    billingClient.querySkuDetailsAsync(params) { billingResult1, skuDetailsList ->
//                        when (billingResult1.responseCode) {
//                            BillingClient.BillingResponseCode.OK -> {
//                                emitter.onNext(SkuDetailsResponse.Data(skuDetailsList.orEmpty()))
//                                emitter.onComplete()
//                            }
//                            else -> {
//                                emitter.onNext(SkuDetailsResponse.Error(billingResult1.responseCode))
//                            }
//                        }
//                    }
//                }
//            }
//    }

//    fun getPurchases(): Single<PurchasesResponse> {
//        return connect()
//            .flatMap { billingClient ->
//                Observable.create<PurchasesResponse> { emitter ->
//                    val response = billingClient.queryPurchases(BillingClient.SkuType.INAPP)
//                    when (response.billingResult.responseCode) {
//                        BillingClient.BillingResponseCode.OK -> {
//                            emitter.onNext(PurchasesResponse.Data(response.purchasesList.orEmpty()))
//                            emitter.onComplete()
//                        }
//                        else -> {
//                            emitter.onNext(PurchasesResponse.Error(response.billingResult.responseCode))
//                        }
//                    }
//                }
//            }
//    }

    private fun connect(): Single<BillingClient> = Single.create { emitter ->
        if (billingClient == null || billingClient?.isReady == false) {
            val client = BillingClient.newBuilder(context.applicationContext)
                .enablePendingPurchases()
                .setListener { responseCode, purchases -> Timber.d("howiye") }
//                .setListener { responseCode, purchases -> purchaseSubject.onNext(PurchasesUpdate(responseCode, purchases)) }
                .build()

            billingClient = client

            client.startConnection(
                object : BillingClientStateListener {

                    override fun onBillingSetupFinished(billingResult: BillingResult) {
                        Timber.d("${object{}.javaClass.enclosingMethod?.name}")
                        when (billingResult.responseCode) {
                            BillingClient.BillingResponseCode.OK -> {
                                Timber.d("${object{}.javaClass.enclosingMethod?.name} OK")
                                emitter.onSuccess(client)
                            }
                            else -> {
                                Timber.d("${object{}.javaClass.enclosingMethod?.name} UNKNOWN")
                                Timber.d(billingResult.debugMessage)
                                billingClient = null
                            }
                        }
                    }

                    override fun onBillingServiceDisconnected() {
                        billingClient = null // We'll build up a new connection upon next request.
                    }
                }
            )
        } else {
            emitter.onSuccess(requireNotNull(billingClient))
        }
    }

    fun disconnect() {
        billingClient?.endConnection()
        billingClient = null
    }
}

sealed class SkuDetailsResponse {

    data class Data(val skuDetails: List<SkuDetails>) : SkuDetailsResponse()

    data class Error(val responseCode: Int) : SkuDetailsResponse()
}

sealed class PurchasesResponse {

    data class Data(val skuDetails: List<Purchase>) : PurchasesResponse()

    data class Error(val responseCode: Int) : PurchasesResponse()
}

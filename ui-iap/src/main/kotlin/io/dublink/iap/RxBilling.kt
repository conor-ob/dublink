package io.dublink.iap

import android.app.Activity
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.FeatureType
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchaseHistoryRecord
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import io.dublink.domain.service.RxScheduler
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.subjects.PublishSubject
import java.lang.ref.WeakReference

interface RxBilling : Connectable<BillingClient> {

    override fun connect(): Flowable<BillingClient>

    fun isFeatureSupported(@FeatureType feature: String): Single<Boolean>

    fun observeUpdates(): Flowable<PurchasesUpdate>

    fun getPurchases(@BillingClient.SkuType skuType: String): Single<List<Purchase>>

    fun getPurchaseHistory(@BillingClient.SkuType skuType: String): Single<List<PurchaseHistoryRecord>>

    fun getSkuDetails(params: SkuDetailsParams): Single<List<SkuDetails>>

    fun launchFlow(activity: Activity, params: BillingFlowParams): Completable

    fun consumeProduct(params: ConsumeParams): Completable

    fun acknowledge(params: AcknowledgePurchaseParams): Completable
}

class RxBillingImpl(
    billingFactory: BillingClientFactory,
    scheduler: RxScheduler
) : RxBilling {

    private val updateSubject = PublishSubject.create<PurchasesUpdate>()

    private val updatedListener = PurchasesUpdatedListener { result, purchases ->
        val event = when (val responseCode = result.responseCode) {
            BillingClient.BillingResponseCode.OK -> PurchasesUpdate.Success(responseCode, purchases.orEmpty())
            BillingClient.BillingResponseCode.USER_CANCELED -> PurchasesUpdate.Canceled(responseCode, purchases.orEmpty())
            else -> PurchasesUpdate.Failed(responseCode, purchases.orEmpty())
        }
        updateSubject.onNext(event)
    }

    private val connectionFlowable =
        Completable.complete()
            .observeOn(scheduler.ui) // just to be sure billing client is called from main thread
            .andThen(billingFactory.createBillingFlowable(updatedListener))

    override fun connect(): Flowable<BillingClient> {
        return connectionFlowable
    }

    override fun isFeatureSupported(@FeatureType feature: String): Single<Boolean> {
        return connectionFlowable.flatMapSingle {
            Single.defer {
                val result = it.isFeatureSupported(feature)
                Single.just(result.responseCode == BillingClient.BillingResponseCode.OK)
            }
        }.firstOrError()
    }

    override fun observeUpdates(): Flowable<PurchasesUpdate> {
        return connectionFlowable.flatMap {
            updateSubject.toFlowable(BackpressureStrategy.LATEST)
        }
    }

    override fun getPurchases(@BillingClient.SkuType skuType: String): Single<List<Purchase>> {
        return getBoughtItems(skuType)
    }

    override fun getPurchaseHistory(@BillingClient.SkuType skuType: String): Single<List<PurchaseHistoryRecord>> {
        return getHistory(skuType)
    }

    override fun getSkuDetails(params: SkuDetailsParams): Single<List<SkuDetails>> {
        return connectionFlowable
                .flatMapSingle { client ->
                    Single.create<List<SkuDetails>> {
                        val emitter = WeakReference<SingleEmitter<List<SkuDetails>>>(it)
                        client.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
                            val observer = emitter.get()
                            if (observer != null && observer.isDisposed) {
                                return@querySkuDetailsAsync
                            } else if (observer != null && !observer.isDisposed) {
                                val responseCode = billingResult.responseCode
                                if (isSuccess(responseCode)) {
                                    observer.onSuccess(skuDetailsList.orEmpty())
                                } else {
                                    observer.onError(BillingException.fromResult(billingResult))
                                }
                            }
                        }
                    }
                }.firstOrError()
    }

    override fun launchFlow(activity: Activity, params: BillingFlowParams): Completable {
        return connectionFlowable
            .flatMap {
                val responseCode = it.launchBillingFlow(activity, params)
                return@flatMap Flowable.just(responseCode)
            }
            .firstOrError()
            .flatMapCompletable {
                return@flatMapCompletable if (isSuccess(it.responseCode)) {
                    Completable.complete()
                } else {
                    Completable.error(BillingException.fromResult(it))
                }
            }
    }

    override fun consumeProduct(params: ConsumeParams): Completable {
        return connectionFlowable
            .flatMapSingle { client ->
                Single.create<Int> {
                    val emitter = WeakReference<SingleEmitter<Int>>(it)
                    client.consumeAsync(params) { result, _ ->
                        val observer = emitter.get()
                        if (observer != null && observer.isDisposed) {
                            return@consumeAsync
                        } else if (observer != null && !observer.isDisposed) {
                            val responseCode = result.responseCode
                            if (isSuccess(responseCode)) {
                                observer.onSuccess(responseCode)
                            } else {
                                observer.onError(BillingException.fromResult(result))
                            }
                        }
                    }
                }
            }
            .firstOrError()
            .ignoreElement()
    }

    override fun acknowledge(params: AcknowledgePurchaseParams): Completable {
        return connectionFlowable
            .flatMapSingle { client ->
                Single.create<Int> {
                    val emitter = WeakReference<SingleEmitter<Int>>(it)
                    client.acknowledgePurchase(params) { result ->
                        val observer = emitter.get()
                        if (observer != null && observer.isDisposed) {
                            return@acknowledgePurchase
                        } else if (observer != null && !observer.isDisposed) {
                            val responseCode = result.responseCode
                            if (isSuccess(responseCode)) {
                                observer.onSuccess(responseCode)
                            } else {
                                observer.onError(BillingException.fromResult(result))
                            }
                        }
                    }
                }
            }
            .firstOrError()
            .ignoreElement()
    }

    private fun getBoughtItems(@BillingClient.SkuType type: String): Single<List<Purchase>> {
        return connectionFlowable
            .flatMapSingle {
                val purchasesResult = it.queryPurchases(type)
                return@flatMapSingle if (isSuccess(purchasesResult.responseCode)) {
                    Single.just(purchasesResult.purchasesList.orEmpty())
                } else {
                    Single.error(BillingException.fromResult(purchasesResult.billingResult))
                }
            }.firstOrError()
    }

    private fun getHistory(@BillingClient.SkuType type: String): Single<List<PurchaseHistoryRecord>> {
        return connectionFlowable
            .flatMapSingle { client ->
                Single.create<List<PurchaseHistoryRecord>> {
                    client.queryPurchaseHistoryAsync(type) { billingResult: BillingResult, list: MutableList<PurchaseHistoryRecord>? ->
                        if (it.isDisposed) return@queryPurchaseHistoryAsync
                        val responseCode = billingResult.responseCode
                        if (isSuccess(responseCode)) {
                            it.onSuccess(list.orEmpty())
                        } else {
                            it.onError(BillingException.fromResult(billingResult))
                        }
                    }
                }
            }.firstOrError()
    }

    private fun isSuccess(responseCode: Int): Boolean {
        return responseCode == BillingClient.BillingResponseCode.OK
    }
}

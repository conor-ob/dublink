package io.dublink.iap

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class InAppPurchaseViewModel @Inject constructor(
    private val reactiveBillingClient: ReactiveBillingClient
) : ViewModel() {

    private val disposables = CompositeDisposable()

    val dubLinkProPrice = MutableLiveData<String?>()

    fun start() {
        disposables.add(
            reactiveBillingClient
                .getSkuDetails()
//                .doOnSuccess { response ->
//                    when (response) {
//                        is SkuDetailsResponse.Data -> {
//                            dubLinkProPrice.value = response.skuDetails.find { it.sku == InAppPurchaseRepository.DubLinkSku.DUBLINK_PRO.productId }?.price
//                        }
//                        is SkuDetailsResponse.Error -> {
//
//                        }
//                    }
//                }
//                .doOnError {  }
                .subscribe { response ->
                    if (disposables.isDisposed) return@subscribe
                    when (response) {
                        is SkuDetailsResponse.Data -> {
                            dubLinkProPrice.value = response.skuDetails.find { it.sku == InAppPurchaseRepository.DubLinkSku.DUBLINK_PRO.productId }?.price
                        }
                        is SkuDetailsResponse.Error -> {

                        }
                    }
                }
        )
    }

    fun stop() {
        disposables.clear()
        disposables.dispose()
    }
}

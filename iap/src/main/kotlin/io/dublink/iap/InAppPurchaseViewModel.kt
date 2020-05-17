package io.dublink.iap

import android.app.Activity
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class InAppPurchaseViewModel @Inject constructor(
    private val inAppPurchaseRepository: InAppPurchaseRepository
) : ViewModel() {

    val dubLinkProPriceLiveData = inAppPurchaseRepository.dubLinkProPriceLiveData

    fun start() {
        inAppPurchaseRepository.startDataSourceConnections()
    }

    fun onBuy(activity: Activity) {
        inAppPurchaseRepository.launchBillingFlow(activity)
    }

    override fun onCleared() {
        super.onCleared()
        inAppPurchaseRepository.endDataSourceConnections()
    }
}

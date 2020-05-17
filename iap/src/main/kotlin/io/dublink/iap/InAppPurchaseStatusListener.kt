package io.dublink.iap

interface InAppPurchaseStatusListener {

    fun onPurchaseStarted()

    fun onPurchaseSuccessful()

    fun onPurchaseError()
}

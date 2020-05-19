package io.dublink.iap

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class InAppPurchaseModule {

    @Provides
    @Singleton
    fun reactiveBillingClient(
        context: Context
    ): ReactiveBillingClient = ReactiveBillingClient(context)

    @Provides
    @Singleton
    fun rxBilling(
        context: Context
    ): RxBilling = RxBillingImpl(BillingClientFactory(context))
}

package io.dublink.iap

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class InAppPurchaseModule {

    @Provides
    @Singleton
    fun rxBilling(
        context: Context
    ): RxBilling = RxBillingImpl(BillingClientFactory(context))
}

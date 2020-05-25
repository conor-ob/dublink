package io.dublink.iap

import android.content.Context
import dagger.Module
import dagger.Provides
import io.dublink.domain.service.AppConfig
import io.dublink.domain.service.RxScheduler
import javax.inject.Singleton

@Module
class InAppPurchaseModule {

    @Provides
    @Singleton
    fun rxBilling(
        context: Context,
        scheduler: RxScheduler
    ): RxBilling = RxBillingImpl(BillingClientFactory(context), scheduler)

    @Provides
    @Singleton
    fun inAppPurchaseVerifier(
        appConfig: AppConfig
    ): InAppPurchaseVerifier = InAppPurchaseVerifier(appConfig.publicKey())
}

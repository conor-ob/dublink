package io.dublink.iap

import android.content.Context
import dagger.Module
import dagger.Provides
import io.dublink.domain.service.PreferenceStore
import javax.inject.Singleton

@Module
class InAppPurchaseModule {

    @Provides
    @Singleton
    fun inAppPurchaseRepository(
        context: Context,
        preferenceStore: PreferenceStore
    ): InAppPurchaseRepository = InAppPurchaseRepository(context, preferenceStore)

    @Provides
    @Singleton
    fun reactiveBillingClient(
        context: Context
    ): ReactiveBillingClient = ReactiveBillingClient(context)
}

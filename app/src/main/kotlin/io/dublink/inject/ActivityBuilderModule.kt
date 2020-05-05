package io.dublink.inject

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.dublink.DubLinkActivity

@Module
abstract class ActivityBuilderModule {

    @ContributesAndroidInjector(modules = [FragmentBuilderModule::class])
    abstract fun contributeHomeActivityInjector(): DubLinkActivity
}

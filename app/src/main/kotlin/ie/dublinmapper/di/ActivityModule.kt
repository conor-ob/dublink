package ie.dublinmapper.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ie.dublinmapper.DublinMapperActivity

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector(modules = [ApplicationModule::class])
    abstract fun contributeHomeActivityInjector(): DublinMapperActivity

}

package ie.dublinmapper.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ie.dublinmapper.DublinMapperActivity

@Module
abstract class ActivityBuilderModule {

    @ContributesAndroidInjector(modules = [FragmentBuilderModule::class])
    abstract fun contributeHomeActivityInjector(): DublinMapperActivity
}

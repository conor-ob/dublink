package ie.dublinmapper.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ie.dublinmapper.HomeActivity
import ie.dublinmapper.settings.SettingsActivity
import ie.dublinmapper.settings.SettingsModule

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector(modules = [ApplicationModule::class])
    abstract fun contributeHomeActivityInjector(): HomeActivity

    @ContributesAndroidInjector(modules = [SettingsModule::class])
    abstract fun contributeSettingsActivityInjector(): SettingsActivity

}

package ie.dublinmapper.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ie.dublinmapper.favourites.FavouritesFragment
import ie.dublinmapper.favourites.FavouritesModule
import ie.dublinmapper.livedata.LiveDataFragment
import ie.dublinmapper.livedata.LiveDataModule
import ie.dublinmapper.nearby.NearbyFragment
import ie.dublinmapper.nearby.NearbyModule
import ie.dublinmapper.news.NewsFragment
import ie.dublinmapper.news.NewsModule
import ie.dublinmapper.news.TwitterFragment
import ie.dublinmapper.news.TwitterModule
import ie.dublinmapper.search.SearchFragment
import ie.dublinmapper.search.SearchModule
import ie.dublinmapper.settings.PreferencesFragment
import ie.dublinmapper.settings.PreferencesModule
import ie.dublinmapper.settings.SettingsFragment
import ie.dublinmapper.settings.SettingsModule

@Module
abstract class FragmentModule {

    @ContributesAndroidInjector(modules = [FavouritesModule::class])
    abstract fun contributeFavouritesFragmentInjector(): FavouritesFragment

    @ContributesAndroidInjector(modules = [LiveDataModule::class])
    abstract fun contributeLiveDataFragmentInjector(): LiveDataFragment

    @ContributesAndroidInjector(modules = [NewsModule::class])
    abstract fun contributeNewsFragmentInjector(): NewsFragment

    @ContributesAndroidInjector(modules = [TwitterModule::class])
    abstract fun contributeTwitterFragmentInjector(): TwitterFragment

    @ContributesAndroidInjector(modules = [NearbyModule::class])
    abstract fun contributeNearbyFragmentInjector(): NearbyFragment

    @ContributesAndroidInjector(modules = [SearchModule::class])
    abstract fun contributeSearchFragmentInjector(): SearchFragment

    @ContributesAndroidInjector(modules = [PreferencesModule::class])
    abstract fun contributePreferencesFragmentInjector(): PreferencesFragment

    @ContributesAndroidInjector(modules = [SettingsModule::class])
    abstract fun contributeSettingsFragmentInjector(): SettingsFragment
}

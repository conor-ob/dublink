package ie.dublinmapper.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ie.dublinmapper.favourites.FavouritesFragment
import ie.dublinmapper.livedata.LiveDataFragment
import ie.dublinmapper.nearby.NearbyFragment
import ie.dublinmapper.news.NewsFragment
import ie.dublinmapper.news.TwitterFragment
import ie.dublinmapper.search.SearchFragment
import ie.dublinmapper.settings.PreferencesFragment
import ie.dublinmapper.settings.SettingsFragment

@Module
abstract class FragmentBuilderModule {

    @ContributesAndroidInjector
    abstract fun contributeFavouritesFragmentInjector(): FavouritesFragment

    @ContributesAndroidInjector
    abstract fun contributeLiveDataFragmentInjector(): LiveDataFragment

    @ContributesAndroidInjector
    abstract fun contributeNewsFragmentInjector(): NewsFragment

    @ContributesAndroidInjector
    abstract fun contributeTwitterFragmentInjector(): TwitterFragment

    @ContributesAndroidInjector
    abstract fun contributeNearbyFragmentInjector(): NearbyFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchFragmentInjector(): SearchFragment

    @ContributesAndroidInjector
    abstract fun contributePreferencesFragmentInjector(): PreferencesFragment

    @ContributesAndroidInjector
    abstract fun contributeSettingsFragmentInjector(): SettingsFragment
}

package ie.dublinmapper.inject

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ie.dublinmapper.favourites.FavouritesFragment
import ie.dublinmapper.livedata.LiveDataFragment
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
    abstract fun contributeSearchFragmentInjector(): SearchFragment

    @ContributesAndroidInjector
    abstract fun contributePreferencesFragmentInjector(): PreferencesFragment

    @ContributesAndroidInjector
    abstract fun contributeSettingsFragmentInjector(): SettingsFragment
}

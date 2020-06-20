package io.dublink.inject

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.dublink.favourites.FavouritesFragment
import io.dublink.favourites.edit.EditFavouritesFragment
import io.dublink.iap.dublinkpro.DubLinkProFragment
import io.dublink.livedata.LiveDataFragment
import io.dublink.nearby.NearbyFragment
import io.dublink.search.SearchFragment
import io.dublink.settings.PreferencesFragment
import io.dublink.settings.SettingsFragment
import io.dublink.web.WebViewFragment

@Module
abstract class FragmentBuilderModule {

    @ContributesAndroidInjector
    abstract fun contributeNearbyFragmentInjector(): NearbyFragment

    @ContributesAndroidInjector
    abstract fun contributeFavouritesFragmentInjector(): FavouritesFragment

    @ContributesAndroidInjector
    abstract fun contributeEditFavouritesFragmentInjector(): EditFavouritesFragment

    @ContributesAndroidInjector
    abstract fun contributeLiveDataFragmentInjector(): LiveDataFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchFragmentInjector(): SearchFragment

    @ContributesAndroidInjector
    abstract fun contributePreferencesFragmentInjector(): PreferencesFragment

    @ContributesAndroidInjector
    abstract fun contributeSettingsFragmentInjector(): SettingsFragment

    @ContributesAndroidInjector
    abstract fun contributeInAppPurchaseFragmentInjector(): DubLinkProFragment

    @ContributesAndroidInjector
    abstract fun contributeWebViewFragmentInjector(): WebViewFragment
}

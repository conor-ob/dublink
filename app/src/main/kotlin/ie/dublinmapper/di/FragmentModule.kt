package ie.dublinmapper.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ie.dublinmapper.view.nearby.NearbyFragment
import ie.dublinmapper.view.nearby.NearbyModule
import ie.dublinmapper.view.search.SearchFragment
import ie.dublinmapper.view.search.SearchModule

@Module
abstract class FragmentModule {

    @ContributesAndroidInjector(modules = [NearbyModule::class])
    abstract fun contributeNearbyFragmentInjector(): NearbyFragment

    @ContributesAndroidInjector(modules = [SearchModule::class])
    abstract fun contributeSearchFragmentInjector(): SearchFragment

}

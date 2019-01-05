package ie.dublinmapper.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ie.dublinmapper.view.nearby.NearbyFragment
import ie.dublinmapper.view.nearby.NearbyModule

@Module
abstract class FragmentModule {

    @ContributesAndroidInjector(modules = [NearbyModule::class])
    abstract fun contributeMapFragmentInjector(): NearbyFragment

}

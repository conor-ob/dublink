package ie.dublinmapper.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ie.dublinmapper.favourites.FavouritesFragment
import ie.dublinmapper.favourites.FavouritesModule
import ie.dublinmapper.livedata.LiveDataFragment
import ie.dublinmapper.livedata.LiveDataModule
import ie.dublinmapper.search.SearchFragment
import ie.dublinmapper.search.SearchModule

@Module
abstract class FragmentModule {

    @ContributesAndroidInjector(modules = [FavouritesModule::class])
    abstract fun contributeFavouritesFragmentInjector(): FavouritesFragment

    @ContributesAndroidInjector(modules = [LiveDataModule::class])
    abstract fun contributeLiveDataFragmentInjector(): LiveDataFragment

    @ContributesAndroidInjector(modules = [SearchModule::class])
    abstract fun contributeSearchFragmentInjector(): SearchFragment

}

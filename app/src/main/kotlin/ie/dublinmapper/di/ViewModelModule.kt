package ie.dublinmapper.di

import dagger.Module
import android.arch.lifecycle.ViewModel
import dagger.multibindings.IntoMap
import dagger.Binds
import android.arch.lifecycle.ViewModelProvider
import ie.dublinmapper.view.ViewModelFactory
import ie.dublinmapper.view.ViewModelKey
import ie.dublinmapper.view.nearby.NearbyViewModel

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(NearbyViewModel::class)
    internal abstract fun bindNearbyViewModel(nearbyViewModel: NearbyViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

}

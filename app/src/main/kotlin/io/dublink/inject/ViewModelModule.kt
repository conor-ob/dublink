package io.dublink.inject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import io.dublink.DubLinkActivityViewModel
import io.dublink.DubLinkFragmentViewModel
import io.dublink.favourites.FavouritesViewModel
import io.dublink.favourites.edit.EditFavouritesViewModel
import io.dublink.iap.dublinkpro.DubLinkProViewModel
import io.dublink.livedata.LiveDataViewModel
import io.dublink.nearby.NearbyViewModel
import io.dublink.search.SearchViewModel
import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kotlin.reflect.KClass

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(DubLinkActivityViewModel::class)
    internal abstract fun bindDubLinkActivityViewModel(dubLinkActivityViewModel: DubLinkActivityViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DubLinkFragmentViewModel::class)
    internal abstract fun bindDubLinkFragmentViewModel(dubLinkFragmentViewModel: DubLinkFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NearbyViewModel::class)
    internal abstract fun bindNearbyViewModel(nearbyViewModel: NearbyViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FavouritesViewModel::class)
    internal abstract fun bindFavouritesViewModel(favouritesViewModel: FavouritesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EditFavouritesViewModel::class)
    internal abstract fun bindEditFavouritesViewModel(editFavouritesViewModel: EditFavouritesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LiveDataViewModel::class)
    internal abstract fun bindLiveDataViewModel(liveDataViewModel: LiveDataViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    internal abstract fun bindSearchViewModel(searchViewModel: SearchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DubLinkProViewModel::class)
    internal abstract fun bindDubLinkProViewModelViewModel(dubLinkProViewModel: DubLinkProViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}

@Suppress("DEPRECATED_JAVA_ANNOTATION")
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@MapKey
internal annotation class ViewModelKey(val value: KClass<out ViewModel>)

@Singleton
class ViewModelFactory @Inject constructor(
    private val creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        var creator: Provider<out ViewModel>? = creators[modelClass]

        if (creator == null) {
            for ((key, value) in creators) {
                if (modelClass.isAssignableFrom(key)) {
                    creator = value
                    break
                }
            }
        }

        if (creator == null) {
            throw IllegalArgumentException("unknown model class $modelClass")
        }

        try {
            return creator.get() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}

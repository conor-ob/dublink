package ie.dublinmapper.inject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import ie.dublinmapper.DublinMapperActivityViewModel
import ie.dublinmapper.favourites.FavouritesViewModel
import ie.dublinmapper.livedata.LiveDataViewModel
import ie.dublinmapper.nearby.NearbyViewModel
import ie.dublinmapper.search.SearchViewModel
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
    @ViewModelKey(DublinMapperActivityViewModel::class)
    internal abstract fun bindDublinMapperActivityViewModel(dublinMapperActivityViewModel: DublinMapperActivityViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FavouritesViewModel::class)
    internal abstract fun bindFavouritesViewModel(favouritesViewModel: FavouritesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LiveDataViewModel::class)
    internal abstract fun bindLiveDataViewModel(liveDataViewModel: LiveDataViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NearbyViewModel::class)
    internal abstract fun bindNearbyViewModel(nearbyViewModel: NearbyViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    internal abstract fun bindsearchViewModel(searchViewModel: SearchViewModel): ViewModel

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

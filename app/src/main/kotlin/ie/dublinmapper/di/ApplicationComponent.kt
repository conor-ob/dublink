package ie.dublinmapper.di

import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import ie.dublinmapper.DublinMapperApplication
import ie.dublinmapper.database.DatabaseModule
import ie.dublinmapper.repository.di.RepositoryModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        ApplicationModule::class,
        ActivityModule::class,
        FragmentModule::class,
        ViewModelModule::class,
        DatabaseModule::class,
        RepositoryModule::class
    ]
)
interface ApplicationComponent : AndroidInjector<DublinMapperApplication> {

    @Component.Factory
    abstract class Factory : AndroidInjector.Factory<DublinMapperApplication>
}

package ie.dublinmapper.di

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import ie.dublinmapper.DublinMapperApplication
import ie.dublinmapper.repository.dublinbikes.DublinBikesRepositoryModule
import ie.dublinmapper.service.di.ServiceModule
import ie.dublinmapper.service.jcdecaux.JcdecauxModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        ApplicationModule::class,
        ActivityModule::class,
        ViewModelModule::class,
        DublinBikesRepositoryModule::class,
        JcdecauxModule::class,
        ServiceModule::class
    ]
)
interface ApplicationComponent : AndroidInjector<DublinMapperApplication> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<DublinMapperApplication>() {

        @BindsInstance
        abstract fun application(application: DublinMapperApplication): Builder

        abstract fun jcdecauxModule(jcdecauxModule: JcdecauxModule): Builder

        abstract fun dublinBikesRepositoryModule(dublinBikesRepositoryModule: DublinBikesRepositoryModule): Builder

    }

}

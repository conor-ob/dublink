package ie.dublinmapper.di

import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import ie.dublinmapper.DublinMapperApplication
import ie.dublinmapper.repository.dublinbikes.DublinBikesRepositoryModule
import ie.dublinmapper.repository.luas.LuasStopRepositoryModule
import ie.dublinmapper.service.di.ServiceModule
import ie.dublinmapper.service.jcdecaux.JcdecauxModule
import ie.dublinmapper.view.viewmodel.ViewModelModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
//        AndroidInjectionModule::class,
        AndroidSupportInjectionModule::class,
        ApplicationModule::class,
        ActivityModule::class,
        FragmentModule::class,
        ViewModelModule::class,
        DublinBikesRepositoryModule::class,
        LuasStopRepositoryModule::class,
        JcdecauxModule::class,
        ServiceModule::class
    ]
)
interface ApplicationComponent : AndroidInjector<DublinMapperApplication> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<DublinMapperApplication>()

}

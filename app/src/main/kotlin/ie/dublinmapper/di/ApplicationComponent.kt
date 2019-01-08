package ie.dublinmapper.di

import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import ie.dublinmapper.DublinMapperApplication
import ie.dublinmapper.repository.dart.DartRepositoryModule
import ie.dublinmapper.repository.dublinbikes.DublinBikesRepositoryModule
import ie.dublinmapper.repository.dublinbus.DublinBusRepositoryModule
import ie.dublinmapper.repository.luas.LuasStopRepositoryModule
import ie.dublinmapper.service.di.ServiceModule
import ie.dublinmapper.service.dublinbus.DublinBusModule
import ie.dublinmapper.service.irishrail.IrishRailModule
import ie.dublinmapper.service.jcdecaux.JcdecauxModule
import ie.dublinmapper.service.rtpi.RtpiModule
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
        DartRepositoryModule::class,
        DublinBikesRepositoryModule::class,
        DublinBusModule::class,
        DublinBusRepositoryModule::class,
        LuasStopRepositoryModule::class,
        IrishRailModule::class,
        JcdecauxModule::class,
        RtpiModule::class,
        ServiceModule::class
    ]
)
interface ApplicationComponent : AndroidInjector<DublinMapperApplication> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<DublinMapperApplication>()

}

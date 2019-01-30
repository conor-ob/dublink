package ie.dublinmapper.di

import dagger.Component
import ie.dublinmapper.repository.dart.DartRepositoryModule
import ie.dublinmapper.repository.dublinbikes.DublinBikesRepositoryModule
import ie.dublinmapper.repository.dublinbus.DublinBusRepositoryModule
import ie.dublinmapper.repository.luas.LuasRepositoryModule
import ie.dublinmapper.repository.swordsexpress.SwordsExpressRepositoryModule
import ie.dublinmapper.service.di.ServiceModule
import ie.dublinmapper.service.dublinbus.DublinBusModule
import ie.dublinmapper.service.irishrail.IrishRailModule
import ie.dublinmapper.service.jcdecaux.JcDecauxModule
import ie.dublinmapper.service.rtpi.RtpiModule
import ie.dublinmapper.service.swordsexpress.SwordsExpressModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApplicationModule::class,
        DartRepositoryModule::class,
        DublinBikesRepositoryModule::class,
        DublinBusRepositoryModule::class,
        LuasRepositoryModule::class,
        SwordsExpressRepositoryModule::class,
        DublinBusModule::class,
        IrishRailModule::class,
        JcDecauxModule::class,
        RtpiModule::class,
        SwordsExpressModule::class,
        ServiceModule::class
    ]
)
interface BuildVariantApplicationComponent : ApplicationComponent

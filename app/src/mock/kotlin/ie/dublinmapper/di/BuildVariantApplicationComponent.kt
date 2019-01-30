package ie.dublinmapper.di

import dagger.Component
import ie.dublinmapper.repository.dart.DartRepositoryModule
import ie.dublinmapper.repository.dublinbikes.DublinBikesRepositoryModule
import ie.dublinmapper.repository.dublinbus.DublinBusRepositoryModule
import ie.dublinmapper.repository.luas.LuasRepositoryModule
import ie.dublinmapper.service.dublinbus.MockDublinBusModule
import ie.dublinmapper.service.irishrail.MockIrishRailModule
import ie.dublinmapper.service.jcdecaux.MockJcDecauxModule
import ie.dublinmapper.service.rtpi.MockRtpiModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApplicationModule::class,
        DartRepositoryModule::class,
        DublinBikesRepositoryModule::class,
        DublinBusRepositoryModule::class,
        LuasRepositoryModule::class,
        MockDublinBusModule::class,
        MockIrishRailModule::class,
        MockJcDecauxModule::class,
        MockRtpiModule::class
    ]
)
interface BuildVariantApplicationComponent : ApplicationComponent

package ie.dublinmapper.repository.di

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import dagger.Module
import dagger.Provides
import ie.dublinmapper.domain.repository.LocationRepository
import ie.dublinmapper.domain.service.EnabledServiceManager
import ie.dublinmapper.repository.AggregatedServiceLocationRepository
import ie.dublinmapper.repository.aircoach.AircoachRepositoryModule
import ie.dublinmapper.repository.buseireann.BusEireannRepositoryModule
import ie.dublinmapper.repository.dublinbikes.DublinBikesRepositoryModule
import ie.dublinmapper.repository.dublinbus.DublinBusRepositoryModule
import ie.dublinmapper.repository.favourite.FavouriteRepositoryModule
import ie.dublinmapper.repository.irishrail.IrishRailRepositoryModule
import ie.dublinmapper.repository.luas.LuasRepositoryModule
import io.rtpi.api.Service
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module(
    includes = [
        AircoachRepositoryModule::class,
        BusEireannRepositoryModule::class,
        IrishRailRepositoryModule::class,
        DublinBikesRepositoryModule::class,
        DublinBusRepositoryModule::class,
        LuasRepositoryModule::class,
        FavouriteRepositoryModule::class
    ]
)
class RepositoryModule {

    @Provides
    @Singleton
    @Named("SERVICE_LOCATION")
    fun locationRepository(
        @Named("AIRCOACH") aircoachLocationRepository: LocationRepository,
        @Named("BUS_EIREANN") busEireannLocationRepository: LocationRepository,
        @Named("DUBLIN_BIKES") dublinBikesLocationRepository: LocationRepository,
        @Named("DUBLIN_BUS") dublinBusLocationRepository: LocationRepository,
        @Named("IRISH_RAIL") irishRailLocationRepository: LocationRepository,
        @Named("LUAS") luasLocationRepository: LocationRepository,
        enabledServiceManager: EnabledServiceManager
    ): LocationRepository {
        return AggregatedServiceLocationRepository(
            locationRepositories = mapOf(
                Service.AIRCOACH to aircoachLocationRepository,
                Service.BUS_EIREANN to busEireannLocationRepository,
                Service.DUBLIN_BIKES to dublinBikesLocationRepository,
                Service.DUBLIN_BUS to dublinBusLocationRepository,
                Service.IRISH_RAIL to irishRailLocationRepository,
                Service.LUAS to luasLocationRepository
            ),
            enabledServiceManager = enabledServiceManager
        )
    }

    @Provides
    @Singleton
    @Named("SHORT_TERM")
    fun shortTermMemoryPolicy(): MemoryPolicy {
        return newMemoryPolicy(1, TimeUnit.MINUTES)
    }

    @Provides
    @Singleton
    @Named("LONG_TERM")
    fun longTermMemoryPolicy(): MemoryPolicy {
        return newMemoryPolicy(1, TimeUnit.DAYS)
    }

    private fun newMemoryPolicy(value: Long, timeUnit: TimeUnit): MemoryPolicy {
        return MemoryPolicy.builder()
            .setExpireAfterWrite(value)
            .setExpireAfterTimeUnit(timeUnit)
            .build()
    }
}

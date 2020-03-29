package ie.dublinmapper.repository.inject

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import dagger.Module
import dagger.Provides
import ie.dublinmapper.domain.repository.LiveDataRepository
import ie.dublinmapper.domain.repository.LocationRepository
import ie.dublinmapper.domain.service.EnabledServiceManager
import ie.dublinmapper.repository.AggregatedLiveDataRepository
import ie.dublinmapper.repository.AggregatedLocationRepository
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
        return AggregatedLocationRepository(
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
    @Named("LIVE_DATA")
    fun liveDataRepository(
        @Named("AIRCOACH") aircoachLiveDataRepository: LiveDataRepository,
        @Named("BUS_EIREANN") busEireannLiveDataRepository: LiveDataRepository,
        @Named("DUBLIN_BIKES") dublinBikesLiveDataRepository: LiveDataRepository,
        @Named("DUBLIN_BUS") dublinBusLiveDataRepository: LiveDataRepository,
        @Named("IRISH_RAIL") irishRailLiveDataRepository: LiveDataRepository,
        @Named("LUAS") luasLiveDataRepository: LiveDataRepository
    ): LiveDataRepository {
        return AggregatedLiveDataRepository(
            liveDataRepositories = mapOf(
                Service.AIRCOACH to aircoachLiveDataRepository,
                Service.BUS_EIREANN to busEireannLiveDataRepository,
                Service.DUBLIN_BIKES to dublinBikesLiveDataRepository,
                Service.DUBLIN_BUS to dublinBusLiveDataRepository,
                Service.IRISH_RAIL to irishRailLiveDataRepository,
                Service.LUAS to luasLiveDataRepository
            )
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

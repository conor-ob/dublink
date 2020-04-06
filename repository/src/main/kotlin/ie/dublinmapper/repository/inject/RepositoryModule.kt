package ie.dublinmapper.repository.inject

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import dagger.Module
import dagger.Provides
import ie.dublinmapper.domain.repository.AggregatedLiveDataRepository
import ie.dublinmapper.domain.repository.AggregatedServiceLocationRepository
import ie.dublinmapper.domain.repository.LiveDataRepository
import ie.dublinmapper.domain.repository.ServiceLocationRepository
import ie.dublinmapper.domain.service.EnabledServiceManager
import ie.dublinmapper.repository.DelegatingLiveDataRepository
import ie.dublinmapper.repository.DefaultAggregatedServiceLocationRepository
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
    fun locationRepository(
        @Named("AIRCOACH") aircoachServiceLocationRepository: ServiceLocationRepository,
        @Named("BUS_EIREANN") busEireannServiceLocationRepository: ServiceLocationRepository,
        @Named("DUBLIN_BIKES") dublinBikesServiceLocationRepository: ServiceLocationRepository,
        @Named("DUBLIN_BUS") dublinBusServiceLocationRepository: ServiceLocationRepository,
        @Named("IRISH_RAIL") irishRailServiceLocationRepository: ServiceLocationRepository,
        @Named("LUAS") luasServiceLocationRepository: ServiceLocationRepository,
        enabledServiceManager: EnabledServiceManager
    ): AggregatedServiceLocationRepository {
        return DefaultAggregatedServiceLocationRepository(
            serviceLocationRepositories = mapOf(
                Service.AIRCOACH to aircoachServiceLocationRepository,
                Service.BUS_EIREANN to busEireannServiceLocationRepository,
                Service.DUBLIN_BIKES to dublinBikesServiceLocationRepository,
                Service.DUBLIN_BUS to dublinBusServiceLocationRepository,
                Service.IRISH_RAIL to irishRailServiceLocationRepository,
                Service.LUAS to luasServiceLocationRepository
            ),
            enabledServiceManager = enabledServiceManager
        )
    }

    @Provides
    @Singleton
    fun liveDataRepository(
        @Named("AIRCOACH") aircoachLiveDataRepository: LiveDataRepository,
        @Named("BUS_EIREANN") busEireannLiveDataRepository: LiveDataRepository,
        @Named("DUBLIN_BIKES") dublinBikesLiveDataRepository: LiveDataRepository,
        @Named("DUBLIN_BUS") dublinBusLiveDataRepository: LiveDataRepository,
        @Named("IRISH_RAIL") irishRailLiveDataRepository: LiveDataRepository,
        @Named("LUAS") luasLiveDataRepository: LiveDataRepository
    ): AggregatedLiveDataRepository {
        return DelegatingLiveDataRepository(
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
        return newMemoryPolicy(15, TimeUnit.SECONDS)
    }

    @Provides
    @Singleton
    @Named("MEDIUM_TERM")
    fun mediumTermMemoryPolicy(): MemoryPolicy {
        return newMemoryPolicy(90, TimeUnit.SECONDS)
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

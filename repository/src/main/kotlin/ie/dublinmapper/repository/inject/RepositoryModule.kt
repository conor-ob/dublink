package ie.dublinmapper.repository.inject

import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import com.nytimes.android.external.store3.base.impl.StalePolicy
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import dagger.Module
import dagger.Provides
import ie.dublinmapper.domain.datamodel.FavouriteServiceLocationLocalResource
import ie.dublinmapper.domain.datamodel.RecentServiceLocationSearchLocalResource
import ie.dublinmapper.domain.datamodel.ServiceLocationLocalResource
import ie.dublinmapper.domain.datamodel.ServiceLocationRecordStateLocalResource
import ie.dublinmapper.domain.repository.AggregatedServiceLocationRepository
import ie.dublinmapper.domain.repository.FavouriteRepository
import ie.dublinmapper.domain.repository.LiveDataKey
import ie.dublinmapper.domain.repository.LiveDataRepository
import ie.dublinmapper.domain.repository.RecentServiceLocationSearchRepository
import ie.dublinmapper.domain.repository.ServiceLocationRepository
import ie.dublinmapper.domain.service.EnabledServiceManager
import ie.dublinmapper.domain.service.InternetManager
import ie.dublinmapper.repository.favourite.FavouriteServiceLocationRepository
import ie.dublinmapper.repository.livedata.DefaultLiveDataRepository
import ie.dublinmapper.repository.location.DefaultAggregatedServiceLocationRepository
import ie.dublinmapper.repository.location.DefaultServiceLocationRepository
import ie.dublinmapper.repository.location.ServiceLocationPersister
import ie.dublinmapper.repository.search.DefaultRecentServiceLocationSearchRepository
import io.rtpi.api.LiveData
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation
import io.rtpi.client.RtpiClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun locationRepository(
        rtpiClient: RtpiClient,
        serviceLocationLocalResource: ServiceLocationLocalResource,
        serviceLocationRecordStateLocalResource: ServiceLocationRecordStateLocalResource,
        internetManager: InternetManager,
        enabledServiceManager: EnabledServiceManager
    ): AggregatedServiceLocationRepository {
        return DefaultAggregatedServiceLocationRepository(
            serviceLocationRepositories = Service.values()
                .associateBy { it }
                .mapValues { entry ->
                    newServiceLocationRepository(
                        entry.value,
                        rtpiClient,
                        newServiceLocationMemoryPolicy(entry.value),
                        serviceLocationLocalResource,
                        serviceLocationRecordStateLocalResource,
                        internetManager
                    )
                },
            enabledServiceManager = enabledServiceManager
        )
    }

    @Provides
    @Singleton
    fun liveDataRepository(
        rtpiClient: RtpiClient
    ): LiveDataRepository {
        val fetcher = Fetcher<List<LiveData>, LiveDataKey> {
            rtpiClient.getLiveData(it.service, it.locationId)
        }
        val store = StoreBuilder.key<LiveDataKey, List<LiveData>>()
            .fetcher(fetcher)
            .memoryPolicy(shortTermMemoryPolicy())
            .open()
        return DefaultLiveDataRepository(store)
    }

    private fun newServiceLocationRepository(
        service: Service,
        rtpiClient: RtpiClient,
        memoryPolicy: MemoryPolicy,
        serviceLocationLocalResource: ServiceLocationLocalResource,
        serviceLocationRecordStateLocalResource: ServiceLocationRecordStateLocalResource,
        internetManager: InternetManager
    ): ServiceLocationRepository {
        val fetcher = Fetcher<List<ServiceLocation>, Service> {
            rtpiClient.getServiceLocations(it)
        }
        val persister =
            ServiceLocationPersister(
                memoryPolicy,
                serviceLocationLocalResource,
                serviceLocationRecordStateLocalResource,
                internetManager
            )
        val store = StoreRoom.from(fetcher, persister, StalePolicy.REFRESH_ON_STALE, memoryPolicy)
        return DefaultServiceLocationRepository(
            service,
            store
        )
    }

    private fun shortTermMemoryPolicy(): MemoryPolicy {
        return newMemoryPolicy(1, TimeUnit.SECONDS)
    }

    private fun mediumTermMemoryPolicy(): MemoryPolicy {
        return newMemoryPolicy(90, TimeUnit.SECONDS)
    }

    private fun longTermMemoryPolicy(): MemoryPolicy {
        return newMemoryPolicy(1, TimeUnit.DAYS)
    }

    private fun newServiceLocationMemoryPolicy(service: Service): MemoryPolicy {
        return when (service) {
            Service.AIRCOACH -> longTermMemoryPolicy()
            Service.BUS_EIREANN -> longTermMemoryPolicy()
            Service.DUBLIN_BIKES -> mediumTermMemoryPolicy()
            Service.DUBLIN_BUS -> longTermMemoryPolicy()
            Service.IRISH_RAIL -> longTermMemoryPolicy()
            Service.LUAS -> longTermMemoryPolicy()
        }
    }

    private fun newMemoryPolicy(value: Long, timeUnit: TimeUnit): MemoryPolicy {
        return MemoryPolicy.builder()
            .setExpireAfterWrite(value)
            .setExpireAfterTimeUnit(timeUnit)
            .build()
    }

    @Provides
    @Singleton
    fun provideFavouriteRepository(
        localResource: FavouriteServiceLocationLocalResource
    ): FavouriteRepository {
        return FavouriteServiceLocationRepository(localResource)
    }

    @Provides
    @Singleton
    fun provideRecentSearchRepository(
        recentServiceLocationSearchLocalResource: RecentServiceLocationSearchLocalResource
    ): RecentServiceLocationSearchRepository = DefaultRecentServiceLocationSearchRepository(
        recentServiceLocationSearchLocalResource
    )
}

package io.dublink.repository.inject

import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import com.nytimes.android.external.store3.base.impl.StalePolicy
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import dagger.Module
import dagger.Provides
import io.dublink.domain.datamodel.FavouriteServiceLocationLocalResource
import io.dublink.domain.datamodel.RecentServiceLocationSearchLocalResource
import io.dublink.domain.datamodel.ServiceLocationLocalResource
import io.dublink.domain.datamodel.ServiceLocationRecordStateLocalResource
import io.dublink.domain.repository.AggregatedServiceLocationRepository
import io.dublink.domain.repository.FavouriteRepository
import io.dublink.domain.repository.LiveDataKey
import io.dublink.domain.repository.LiveDataRepository
import io.dublink.domain.repository.RecentServiceLocationSearchRepository
import io.dublink.domain.repository.ServiceLocationRepository
import io.dublink.domain.service.EnabledServiceManager
import io.dublink.domain.service.InternetManager
import io.dublink.domain.util.AppConstants
import io.dublink.repository.favourite.FavouriteServiceLocationRepository
import io.dublink.repository.livedata.DefaultLiveDataRepository
import io.dublink.repository.location.DefaultAggregatedServiceLocationRepository
import io.dublink.repository.location.DefaultServiceLocationRepository
import io.dublink.repository.location.ServiceLocationPersister
import io.dublink.repository.search.DefaultRecentServiceLocationSearchRepository
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
        return newMemoryPolicy(AppConstants.liveDataCacheExpiry.seconds, TimeUnit.SECONDS)
    }

    private fun mediumTermMemoryPolicy(): MemoryPolicy {
        return newMemoryPolicy(AppConstants.dockLocationCacheExpiry.seconds, TimeUnit.SECONDS)
    }

    private fun longTermMemoryPolicy(): MemoryPolicy {
        return newMemoryPolicy(AppConstants.stopLocationCacheExpiry.toDays(), TimeUnit.DAYS)
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
        localResource: FavouriteServiceLocationLocalResource,
        enabledServiceManager: EnabledServiceManager
    ): FavouriteRepository {
        return FavouriteServiceLocationRepository(localResource, enabledServiceManager)
    }

    @Provides
    @Singleton
    fun provideRecentSearchRepository(
        recentServiceLocationSearchLocalResource: RecentServiceLocationSearchLocalResource
    ): RecentServiceLocationSearchRepository = DefaultRecentServiceLocationSearchRepository(
        recentServiceLocationSearchLocalResource
    )
}

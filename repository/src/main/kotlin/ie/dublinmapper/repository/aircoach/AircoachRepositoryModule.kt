package ie.dublinmapper.repository.aircoach

import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import com.nytimes.android.external.store3.base.impl.StalePolicy
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import dagger.Module
import dagger.Provides
import ie.dublinmapper.domain.datamodel.AircoachStopLocalResource
import ie.dublinmapper.domain.datamodel.ServiceLocationRecordStateLocalResource
import ie.dublinmapper.domain.repository.LiveDataRepository
import ie.dublinmapper.domain.repository.LocationRepository
import ie.dublinmapper.domain.service.InternetManager
import ie.dublinmapper.repository.ServiceLiveDataRepository
import ie.dublinmapper.repository.ServiceLocationRepository
import io.rtpi.api.AircoachLiveData
import io.rtpi.api.AircoachStop
import io.rtpi.api.Service
import io.rtpi.client.RtpiClient
import javax.inject.Named
import javax.inject.Singleton

@Module
class AircoachRepositoryModule {

    @Provides
    @Singleton
    @Named("AIRCOACH")
    fun aircoachStopRepository(
        client: RtpiClient,
        localResource: AircoachStopLocalResource,
        serviceLocationRecordStateLocalResource: ServiceLocationRecordStateLocalResource,
        internetManager: InternetManager,
        @Named("LONG_TERM") memoryPolicy: MemoryPolicy
    ): LocationRepository {
        val fetcher = Fetcher<List<AircoachStop>, Service> { client.aircoach().getStops() }
        val persister =
            AircoachStopPersister(
                localResource,
                memoryPolicy,
                serviceLocationRecordStateLocalResource,
                internetManager
            )
        val store = StoreRoom.from(fetcher, persister, StalePolicy.REFRESH_ON_STALE, memoryPolicy)
        return ServiceLocationRepository(Service.AIRCOACH, store)
    }

    @Provides
    @Singleton
    @Named("AIRCOACH")
    fun aircoachLiveDataRepository(
        client: RtpiClient,
        @Named("SHORT_TERM") memoryPolicy: MemoryPolicy
    ): LiveDataRepository {
        val store = StoreBuilder.key<String, List<AircoachLiveData>>()
            .fetcher { stopId -> client.aircoach().getLiveData(stopId = stopId) }
            .memoryPolicy(memoryPolicy)
            .open()
        return ServiceLiveDataRepository(store)
    }
}

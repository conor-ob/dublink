package ie.dublinmapper.repository.dublinbus

import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import com.nytimes.android.external.store3.base.impl.StalePolicy
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import dagger.Module
import dagger.Provides
import ie.dublinmapper.domain.datamodel.DublinBusStopLocalResource
import ie.dublinmapper.domain.datamodel.ServiceLocationRecordStateLocalResource
import ie.dublinmapper.domain.repository.LiveDataRepository
import ie.dublinmapper.domain.repository.LocationRepository
import ie.dublinmapper.domain.service.InternetManager
import ie.dublinmapper.repository.ServiceLiveDataRepository
import ie.dublinmapper.repository.ServiceLocationRepository
import io.rtpi.api.DublinBusLiveData
import io.rtpi.api.DublinBusStop
import io.rtpi.api.Service
import io.rtpi.client.RtpiClient
import javax.inject.Named
import javax.inject.Singleton

@Module
class DublinBusRepositoryModule {

    @Provides
    @Singleton
    @Named("DUBLIN_BUS")
    fun dublinBusStopRepository(
        client: RtpiClient,
        localResource: DublinBusStopLocalResource,
        serviceLocationRecordStateLocalResource: ServiceLocationRecordStateLocalResource,
        internetManager: InternetManager,
        @Named("LONG_TERM") memoryPolicy: MemoryPolicy
    ): LocationRepository {
        val fetcher = Fetcher<List<DublinBusStop>, Service> { client.dublinBus().getStops() }
        val persister =
            DublinBusStopPersister(
                localResource,
                memoryPolicy,
                serviceLocationRecordStateLocalResource,
                internetManager
            )
        val store = StoreRoom.from(fetcher, persister, StalePolicy.REFRESH_ON_STALE, memoryPolicy)
        return ServiceLocationRepository(Service.DUBLIN_BUS, store)
    }

    @Provides
    @Singleton
    @Named("DUBLIN_BUS")
    fun dublinBusLiveDataRepository(
        client: RtpiClient,
        @Named("SHORT_TERM") memoryPolicy: MemoryPolicy
    ): LiveDataRepository {
        val store = StoreBuilder.key<String, List<DublinBusLiveData>>()
            .fetcher { stopId -> client.dublinBus().getLiveData(stopId = stopId) }
            .memoryPolicy(memoryPolicy)
            .open()
        return ServiceLiveDataRepository(store)
    }

}

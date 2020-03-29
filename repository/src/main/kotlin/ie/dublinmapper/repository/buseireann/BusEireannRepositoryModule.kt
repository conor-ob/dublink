package ie.dublinmapper.repository.buseireann

import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import com.nytimes.android.external.store3.base.impl.StalePolicy
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import dagger.Module
import dagger.Provides
import ie.dublinmapper.domain.datamodel.BusEireannStopLocalResource
import ie.dublinmapper.domain.datamodel.ServiceLocationRecordStateLocalResource
import ie.dublinmapper.domain.repository.LocationRepository
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.repository.buseireann.livedata.BusEireannLiveDataRepository
import ie.dublinmapper.repository.buseireann.stops.BusEireannStopPersister
import ie.dublinmapper.domain.service.InternetManager
import ie.dublinmapper.repository.ServiceLocationRepository
import io.rtpi.api.BusEireannLiveData
import io.rtpi.api.BusEireannStop
import io.rtpi.api.Service
import io.rtpi.client.RtpiClient
import javax.inject.Named
import javax.inject.Singleton

@Module
class BusEireannRepositoryModule {

    @Provides
    @Singleton
    @Named("BUS_EIREANN")
    fun busEireannStopRepository(
        client: RtpiClient,
        localResource: BusEireannStopLocalResource,
        serviceLocationRecordStateLocalResource: ServiceLocationRecordStateLocalResource,
        internetManager: InternetManager,
        @Named("LONG_TERM") memoryPolicy: MemoryPolicy
    ): LocationRepository {
        val fetcher = Fetcher<List<BusEireannStop>, Service> { client.busEireann().getStops() }
        val persister = BusEireannStopPersister(localResource, memoryPolicy, serviceLocationRecordStateLocalResource, internetManager)
        val store = StoreRoom.from(fetcher, persister, StalePolicy.REFRESH_ON_STALE, memoryPolicy)
        return ServiceLocationRepository(Service.BUS_EIREANN, store)
    }

    @Provides
    @Singleton
    fun luasRealTimeDataRepository(
        client: RtpiClient,
        @Named("SHORT_TERM") memoryPolicy: MemoryPolicy
    ): Repository<BusEireannLiveData> {
        val store = StoreBuilder.key<String, List<BusEireannLiveData>>()
            .fetcher { stopId -> client.busEireann().getLiveData(stopId = stopId) }
            .memoryPolicy(memoryPolicy)
            .open()
        return BusEireannLiveDataRepository(store)
    }

}

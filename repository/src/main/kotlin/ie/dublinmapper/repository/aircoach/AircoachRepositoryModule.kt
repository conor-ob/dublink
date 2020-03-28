package ie.dublinmapper.repository.aircoach

import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import com.nytimes.android.external.store3.base.impl.StalePolicy
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import dagger.Module
import dagger.Provides
import ie.dublinmapper.datamodel.AircoachStopLocalResource
import ie.dublinmapper.datamodel.ServiceLocationRecordStateLocalResource
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.repository.aircoach.livedata.AircoachLiveDataRepository
import ie.dublinmapper.repository.aircoach.stops.AircoachStopPersister
import ie.dublinmapper.repository.aircoach.stops.AircoachStopRepository
import ie.dublinmapper.core.EnabledServiceManager
import ie.dublinmapper.core.InternetManager
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
    fun aircoachStopRepository(
        client: RtpiClient,
        localResource: AircoachStopLocalResource,
        serviceLocationRecordStateLocalResource: ServiceLocationRecordStateLocalResource,
        internetManager: InternetManager,
        @Named("LONG_TERM") memoryPolicy: MemoryPolicy,
        enabledServiceManager: EnabledServiceManager
    ): Repository<AircoachStop> {
        val fetcher = Fetcher<List<AircoachStop>, Service> { client.aircoach().getStops() }
        val persister = AircoachStopPersister(localResource, memoryPolicy, serviceLocationRecordStateLocalResource, internetManager)
        val store = StoreRoom.from(fetcher, persister, StalePolicy.REFRESH_ON_STALE, memoryPolicy)
        return AircoachStopRepository(store, enabledServiceManager)
    }

    @Provides
    @Singleton
    fun aircoachLiveDataRepository(
        client: RtpiClient,
        @Named("SHORT_TERM") memoryPolicy: MemoryPolicy
    ): Repository<AircoachLiveData> {
        val store = StoreBuilder.key<String, List<AircoachLiveData>>()
            .fetcher { stopId -> client.aircoach().getLiveData(stopId = stopId) }
            .memoryPolicy(memoryPolicy)
            .open()
        return AircoachLiveDataRepository(store)
    }

}

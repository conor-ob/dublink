package ie.dublinmapper.repository.luas

import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import com.nytimes.android.external.store3.base.impl.StalePolicy
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import dagger.Module
import dagger.Provides
import ie.dublinmapper.datamodel.LuasStopLocalResource
import ie.dublinmapper.datamodel.ServiceLocationRecordStateLocalResource
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.repository.luas.livedata.LuasLiveDataRepository
import ie.dublinmapper.repository.luas.stops.LuasStopPersister
import ie.dublinmapper.repository.luas.stops.LuasStopRepository
import ie.dublinmapper.core.EnabledServiceManager
import ie.dublinmapper.core.InternetManager
import io.rtpi.api.LuasLiveData
import io.rtpi.api.LuasStop
import io.rtpi.api.Service
import io.rtpi.client.RtpiClient
import ma.glasnost.orika.MapperFacade
import javax.inject.Named
import javax.inject.Singleton

@Module
class LuasRepositoryModule {

    @Provides
    @Singleton
    fun luasStopRepository(
        client: RtpiClient,
        localResource: LuasStopLocalResource,
        serviceLocationRecordStateLocalResource: ServiceLocationRecordStateLocalResource,
        internetManager: InternetManager,
        mapper: MapperFacade,
        @Named("LONG_TERM") memoryPolicy: MemoryPolicy,
        enabledServiceManager: EnabledServiceManager
    ): Repository<LuasStop> {
        val fetcher = Fetcher<List<LuasStop>, Service> { client.luas().getStops() }
        val persister = LuasStopPersister(localResource, mapper, memoryPolicy, serviceLocationRecordStateLocalResource, internetManager)
        val store = StoreRoom.from(fetcher, persister, StalePolicy.REFRESH_ON_STALE, memoryPolicy)
        return LuasStopRepository(store, enabledServiceManager)
    }

    @Provides
    @Singleton
    fun luasRealTimeDataRepository(
        client: RtpiClient,
        @Named("SHORT_TERM") memoryPolicy: MemoryPolicy
    ): Repository<LuasLiveData> {
        val store = StoreBuilder.key<String, List<LuasLiveData>>()
            .fetcher { stopId -> client.luas().getLiveData(stopId = stopId) }
            .memoryPolicy(memoryPolicy)
            .open()
        return LuasLiveDataRepository(store)
    }

}

package ie.dublinmapper.repository.luas

import com.dropbox.android.external.store4.MemoryPolicy
import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.store.rx2.fromSingle
import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.impl.MemoryPolicy as Store3MemoryPolicy
import com.nytimes.android.external.store3.base.impl.StalePolicy
import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import dagger.Module
import dagger.Provides
import ie.dublinmapper.domain.datamodel.LuasStopLocalResource
import ie.dublinmapper.domain.datamodel.ServiceLocationRecordStateLocalResource
import ie.dublinmapper.domain.repository.LiveDataRepository
import ie.dublinmapper.domain.repository.LocationRepository
import ie.dublinmapper.domain.service.InternetManager
import ie.dublinmapper.repository.ServiceLiveDataRepository
import ie.dublinmapper.repository.ServiceLocationRepository
import io.rtpi.api.LuasLiveData
import io.rtpi.api.LuasStop
import io.rtpi.api.Service
import io.rtpi.client.RtpiClient
import javax.inject.Named
import javax.inject.Singleton

@Module
class LuasRepositoryModule {

    @Provides
    @Singleton
    @Named("LUAS")
    fun luasStopRepository(
        client: RtpiClient,
        localResource: LuasStopLocalResource,
        serviceLocationRecordStateLocalResource: ServiceLocationRecordStateLocalResource,
        internetManager: InternetManager,
        @Named("LONG_TERM") memoryPolicy: Store3MemoryPolicy
    ): LocationRepository {
        val fetcher = Fetcher<List<LuasStop>, Service> { client.luas().getStops() }
        val persister = LuasStopPersister(
            localResource,
            memoryPolicy,
            serviceLocationRecordStateLocalResource,
            internetManager
        )
        val store = StoreRoom.from(fetcher, persister, StalePolicy.REFRESH_ON_STALE, memoryPolicy)
        return ServiceLocationRepository(Service.LUAS, store)
    }

    @Provides
    @Singleton
    @Named("LUAS")
    fun luasRealTimeDataRepository(
        client: RtpiClient,
        @Named("SHORT_TERM") memoryPolicy: Store3MemoryPolicy
    ): LiveDataRepository {
        val store4 = StoreBuilder.fromSingle { stopId: String ->
            client.luas().getLiveData(stopId = stopId)
        }.cachePolicy(
            MemoryPolicy
                .builder()
                .setExpireAfterWrite(memoryPolicy.expireAfterWrite)
                .build()
        ).build()
        return ServiceLiveDataRepository(store4)
    }

}

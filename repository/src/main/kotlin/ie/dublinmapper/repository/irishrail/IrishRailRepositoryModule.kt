package ie.dublinmapper.repository.irishrail

import com.dropbox.android.external.store4.MemoryPolicy
import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.store.rx2.fromSingle
import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.impl.MemoryPolicy as Store3MemoryPolicy
import com.nytimes.android.external.store3.base.impl.StalePolicy
import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import dagger.Module
import dagger.Provides
import ie.dublinmapper.domain.datamodel.IrishRailStationLocalResource
import ie.dublinmapper.domain.datamodel.ServiceLocationRecordStateLocalResource
import ie.dublinmapper.domain.repository.LiveDataRepository
import ie.dublinmapper.domain.repository.LocationRepository
import ie.dublinmapper.domain.service.InternetManager
import ie.dublinmapper.repository.ServiceLiveDataRepository
import ie.dublinmapper.repository.ServiceLocationRepository
import io.rtpi.api.IrishRailLiveData
import io.rtpi.api.IrishRailStation
import io.rtpi.api.Service
import io.rtpi.client.RtpiClient
import javax.inject.Named
import javax.inject.Singleton

@Module
class IrishRailRepositoryModule {

    @Provides
    @Singleton
    @Named("IRISH_RAIL")
    fun irishRailStationRepository(
        client: RtpiClient,
        localResource: IrishRailStationLocalResource,
        serviceLocationRecordStateLocalResource: ServiceLocationRecordStateLocalResource,
        internetManager: InternetManager,
        @Named("LONG_TERM") memoryPolicy: Store3MemoryPolicy
    ): LocationRepository {
        val fetcher = Fetcher<List<IrishRailStation>, Service> { client.irishRail().getStations() }
        val persister =
            IrishRailStationPersister(
                localResource,
                memoryPolicy,
                serviceLocationRecordStateLocalResource,
                internetManager
            )
        val store = StoreRoom.from(fetcher, persister, StalePolicy.REFRESH_ON_STALE, memoryPolicy)
        return ServiceLocationRepository(Service.IRISH_RAIL, store)
    }

    @Provides
    @Singleton
    @Named("IRISH_RAIL")
    fun irishRailLiveDataRepository(
        client: RtpiClient,
        @Named("SHORT_TERM") memoryPolicy: Store3MemoryPolicy
    ): LiveDataRepository {
        val store4 = StoreBuilder.fromSingle { stationId: String ->
            client.irishRail().getLiveData(stationId)
        }.cachePolicy(
            MemoryPolicy
                .builder()
                .setExpireAfterWrite(memoryPolicy.expireAfterWrite)
                .build()
        ).build()
        return ServiceLiveDataRepository(store4)
    }

}

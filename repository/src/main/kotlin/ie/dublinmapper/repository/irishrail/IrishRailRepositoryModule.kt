package ie.dublinmapper.repository.irishrail

import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import com.nytimes.android.external.store3.base.impl.StalePolicy
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import dagger.Module
import dagger.Provides
import ie.dublinmapper.domain.datamodel.IrishRailStationLocalResource
import ie.dublinmapper.domain.datamodel.ServiceLocationRecordStateLocalResource
import ie.dublinmapper.domain.repository.LiveDataRepository
import ie.dublinmapper.domain.repository.ServiceLocationRepository
import ie.dublinmapper.domain.service.InternetManager
import ie.dublinmapper.repository.DefaultLiveDataRepository
import ie.dublinmapper.repository.DefaultServiceLocationRepository
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
        @Named("LONG_TERM") memoryPolicy: MemoryPolicy
    ): ServiceLocationRepository {
        val fetcher = Fetcher<List<IrishRailStation>, Service> { client.irishRail().getStations() }
        val persister =
            IrishRailStationPersister(
                localResource,
                memoryPolicy,
                serviceLocationRecordStateLocalResource,
                internetManager
            )
        val store = StoreRoom.from(fetcher, persister, StalePolicy.REFRESH_ON_STALE, memoryPolicy)
        return DefaultServiceLocationRepository(Service.IRISH_RAIL, store)
    }

    @Provides
    @Singleton
    @Named("IRISH_RAIL")
    fun irishRailLiveDataRepository(
        client: RtpiClient,
        @Named("SHORT_TERM") memoryPolicy: MemoryPolicy
    ): LiveDataRepository {
        val store = StoreBuilder.key<String, List<IrishRailLiveData>>()
            .fetcher { stationId -> client.irishRail().getLiveData(stationId = stationId) }
            .memoryPolicy(memoryPolicy)
            .open()
        return DefaultLiveDataRepository(store)
    }

}

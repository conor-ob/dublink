package ie.dublinmapper.repository.irishrail

import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import com.nytimes.android.external.store3.base.impl.StalePolicy
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import dagger.Module
import dagger.Provides
import ie.dublinmapper.datamodel.irishrail.IrishRailStationLocalResource
import ie.dublinmapper.datamodel.persister.PersisterDao
import ie.dublinmapper.datamodel.persister.ServiceLocationRecordStateLocalResource
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.repository.irishrail.livedata.IrishRailLiveDataRepository
import ie.dublinmapper.repository.irishrail.stations.IrishRailStationPersister
import ie.dublinmapper.repository.irishrail.stations.IrishRailStationRepository
import ie.dublinmapper.util.EnabledServiceManager
import ie.dublinmapper.util.InternetManager
import io.reactivex.Single
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
    fun irishRailStationRepository(
        client: RtpiClient,
        localResource: IrishRailStationLocalResource,
        serviceLocationRecordStateLocalResource: ServiceLocationRecordStateLocalResource,
        internetManager: InternetManager,
        @Named("LONG_TERM") memoryPolicy: MemoryPolicy,
        enabledServiceManager: EnabledServiceManager
    ): Repository<IrishRailStation> {
        val fetcher = Fetcher<List<IrishRailStation>, Service> { client.irishRail().getStations() }
        val persister = IrishRailStationPersister(localResource, memoryPolicy, serviceLocationRecordStateLocalResource, internetManager)
        val store = StoreRoom.from(fetcher, persister, StalePolicy.REFRESH_ON_STALE, memoryPolicy)
        return IrishRailStationRepository(store, enabledServiceManager)
    }

    @Provides
    @Singleton
    fun irishRailLiveDataRepository(
        client: RtpiClient,
        @Named("SHORT_TERM") memoryPolicy: MemoryPolicy
    ): Repository<IrishRailLiveData> {
        val store = StoreBuilder.key<String, List<IrishRailLiveData>>()
            .fetcher { stationId -> client.irishRail().getLiveData(stationId = stationId) }
            .memoryPolicy(memoryPolicy)
            .open()
        return IrishRailLiveDataRepository(store)
    }

}

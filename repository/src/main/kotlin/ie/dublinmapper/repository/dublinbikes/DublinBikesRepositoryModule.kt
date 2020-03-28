package ie.dublinmapper.repository.dublinbikes

import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import com.nytimes.android.external.store3.base.impl.StalePolicy
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import dagger.Module
import dagger.Provides
import ie.dublinmapper.domain.datamodel.DublinBikesDockLocalResource
import ie.dublinmapper.domain.datamodel.ServiceLocationRecordStateLocalResource
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.repository.dublinbikes.docks.DublinBikesDockPersister
import ie.dublinmapper.repository.dublinbikes.docks.DublinBikesDockRepository
import ie.dublinmapper.repository.dublinbikes.livedata.DublinBikesLiveDataRepository
import ie.dublinmapper.core.EnabledServiceManager
import ie.dublinmapper.core.InternetManager
import ie.dublinmapper.core.StringProvider
import io.rtpi.api.DublinBikesDock
import io.rtpi.api.DublinBikesLiveData
import io.rtpi.api.Service
import io.rtpi.client.RtpiClient
import javax.inject.Named
import javax.inject.Singleton

@Module
class DublinBikesRepositoryModule {

    @Provides
    @Singleton
    fun dublinBikesDockRepository(
        client: RtpiClient,
        localResource: DublinBikesDockLocalResource,
        serviceLocationRecordStateLocalResource: ServiceLocationRecordStateLocalResource,
        internetManager: InternetManager,
        stringProvider: StringProvider,
        @Named("SHORT_TERM") memoryPolicy: MemoryPolicy,
        enabledServiceManager: EnabledServiceManager
    ): Repository<DublinBikesDock> {
        val fetcher = Fetcher<List<DublinBikesDock>, Service> { client.dublinBikes().getDocks(stringProvider.jcDecauxApiKey()) }
        val persister = DublinBikesDockPersister(localResource, memoryPolicy, serviceLocationRecordStateLocalResource, internetManager)
        val store = StoreRoom.from(fetcher, persister, StalePolicy.REFRESH_ON_STALE, memoryPolicy)
        return DublinBikesDockRepository(store, enabledServiceManager)
//        val store = StoreBuilder.parsedWithKey<String, List<StationJson>, List<DublinBikesDock>>()
//            .fetcher(fetcher)
//            .parser { docks -> DublinBikesDockMapper.map(docks) }
//            .open()
//        return DublinBikesDockRepository(store)
    }

    @Provides
    @Singleton
    fun dublinBikesRealTimeDataRepository(
        client: RtpiClient,
        stringProvider: StringProvider,
        @Named("SHORT_TERM") memoryPolicy: MemoryPolicy
    ): Repository<DublinBikesLiveData> {
        val store = StoreBuilder.key<String, DublinBikesLiveData>()
            .fetcher { dockId -> client.dublinBikes().getLiveData(dockId = dockId, apiKey = stringProvider.jcDecauxApiKey()) }
            .memoryPolicy(memoryPolicy)
            .open()
        return DublinBikesLiveDataRepository(store)
    }

}

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
import ie.dublinmapper.domain.repository.LiveDataRepository
import ie.dublinmapper.domain.repository.LocationRepository
import ie.dublinmapper.domain.service.InternetManager
import ie.dublinmapper.domain.service.StringProvider
import ie.dublinmapper.repository.ServiceLiveDataRepository
import ie.dublinmapper.repository.ServiceLocationRepository
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
    @Named("DUBLIN_BIKES")
    fun dublinBikesDockRepository(
        client: RtpiClient,
        localResource: DublinBikesDockLocalResource,
        serviceLocationRecordStateLocalResource: ServiceLocationRecordStateLocalResource,
        internetManager: InternetManager,
        stringProvider: StringProvider,
        @Named("SHORT_TERM") memoryPolicy: MemoryPolicy
    ): LocationRepository {
        val fetcher = Fetcher<List<DublinBikesDock>, Service> { client.dublinBikes().getDocks(stringProvider.jcDecauxApiKey()) }
        val persister =
            DublinBikesDockPersister(
                localResource,
                memoryPolicy,
                serviceLocationRecordStateLocalResource,
                internetManager
            )
        val store = StoreRoom.from(fetcher, persister, StalePolicy.REFRESH_ON_STALE, memoryPolicy)
        return ServiceLocationRepository(Service.DUBLIN_BIKES, store)
//        val store = StoreBuilder.parsedWithKey<String, List<StationJson>, List<DublinBikesDock>>()
//            .fetcher(fetcher)
//            .parser { docks -> DublinBikesDockMapper.map(docks) }
//            .open()
//        return DublinBikesDockRepository(store)
    }

    @Provides
    @Singleton
    @Named("DUBLIN_BIKES")
    fun dublinBikesRealTimeDataRepository(
        client: RtpiClient,
        stringProvider: StringProvider,
        @Named("SHORT_TERM") memoryPolicy: MemoryPolicy
    ): LiveDataRepository {
        val store = StoreBuilder.key<String, List<DublinBikesLiveData>>()
            .fetcher { dockId ->
                client.dublinBikes()
                    .getLiveData(dockId = dockId, apiKey = stringProvider.jcDecauxApiKey())
                    .map { listOf(it) }
            }
            .memoryPolicy(memoryPolicy)
            .open()
        return ServiceLiveDataRepository(store)
    }
}

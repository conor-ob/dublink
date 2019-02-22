package ie.dublinmapper.repository.dublinbikes

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import com.nytimes.android.external.store3.base.impl.StalePolicy
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import dagger.Module
import dagger.Provides
import ie.dublinmapper.data.dublinbikes.DublinBikesDockCacheResource
import ie.dublinmapper.data.persister.PersisterDao
import ie.dublinmapper.domain.model.DublinBikesDock
import ie.dublinmapper.domain.model.DublinBikesLiveData
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.repository.dublinbikes.docks.DublinBikesDockFetcher
import ie.dublinmapper.repository.dublinbikes.docks.DublinBikesDockPersister
import ie.dublinmapper.repository.dublinbikes.docks.DublinBikesDockRoomRepository
import ie.dublinmapper.repository.dublinbikes.livedata.DublinBikesLiveDataFetcher
import ie.dublinmapper.repository.dublinbikes.livedata.DublinBikesLiveDataMapper
import ie.dublinmapper.repository.dublinbikes.livedata.DublinBikesLiveDataRepository
import ie.dublinmapper.service.jcdecaux.JcDecauxApi
import ie.dublinmapper.service.jcdecaux.StationJson
import ie.dublinmapper.util.InternetManager
import ie.dublinmapper.util.StringProvider
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class DublinBikesRepositoryModule {

    private val memoryPolicy: MemoryPolicy by lazy { newMemoryPolicy(2, TimeUnit.MINUTES) }

    private fun newMemoryPolicy(value: Long, timeUnit: TimeUnit): MemoryPolicy {
        return MemoryPolicy.builder()
            .setExpireAfterWrite(value)
            .setExpireAfterTimeUnit(timeUnit)
            .build()
    }

    @Provides
    @Singleton
    fun dublinBikesDockRepository(
        api: JcDecauxApi,
        cacheResource: DublinBikesDockCacheResource,
        persisterDao: PersisterDao,
        internetManager: InternetManager,
        stringProvider: StringProvider
    ): Repository<DublinBikesDock> {
        val fetcher = DublinBikesDockFetcher(
            api,
            stringProvider.jcDecauxApiKey(),
            stringProvider.jcDecauxContract()
        )
        val persister = DublinBikesDockPersister(cacheResource, memoryPolicy, persisterDao, internetManager)
        val store = StoreRoom.from(fetcher, persister, StalePolicy.REFRESH_ON_STALE, memoryPolicy)
        return DublinBikesDockRoomRepository(store)
//        val store = StoreBuilder.parsedWithKey<String, List<StationJson>, List<DublinBikesDock>>()
//            .fetcher(fetcher)
//            .parser { docks -> DublinBikesDockMapper.map(docks) }
//            .open()
//        return DublinBikesDockRepository(store)
    }

    @Provides
    @Singleton
    fun dublinBikesRealTimeDataRepository(
        api: JcDecauxApi,
        stringProvider: StringProvider
    ): Repository<DublinBikesLiveData> {
        val fetcher = DublinBikesLiveDataFetcher(
            api,
            stringProvider.jcDecauxApiKey(),
            stringProvider.jcDecauxContract()
        )
        val store = StoreBuilder.parsedWithKey<String, StationJson, DublinBikesLiveData>()
            .fetcher(fetcher)
            .parser { liveData -> DublinBikesLiveDataMapper.map(liveData) }
            .open()
        return DublinBikesLiveDataRepository(store)
    }

}

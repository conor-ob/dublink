package ie.dublinmapper.repository.dublinbikes

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import com.nytimes.android.external.store3.base.impl.StalePolicy
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import dagger.Module
import dagger.Provides
import ie.dublinmapper.datamodel.TxRunner
import ie.dublinmapper.datamodel.aircoach.AircoachStopLocalResource
import ie.dublinmapper.datamodel.dublinbikes.DublinBikesDockDao
import ie.dublinmapper.datamodel.dublinbikes.DublinBikesDockLocalResource
import ie.dublinmapper.datamodel.dublinbikes.DublinBikesDockLocationDao
import ie.dublinmapper.datamodel.dublinbikes.DublinBikesDockServiceDao
import ie.dublinmapper.datamodel.favourite.FavouriteDao
import ie.dublinmapper.datamodel.persister.PersisterDao
import ie.dublinmapper.domain.model.DublinBikesDock
import ie.dublinmapper.domain.model.DublinBikesLiveData
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.repository.dublinbikes.docks.DublinBikesDockFetcher
import ie.dublinmapper.repository.dublinbikes.docks.DublinBikesDockPersister
import ie.dublinmapper.repository.dublinbikes.docks.DublinBikesDockRepository
import ie.dublinmapper.repository.dublinbikes.livedata.DublinBikesLiveDataFetcher
import ie.dublinmapper.repository.dublinbikes.livedata.DublinBikesLiveDataRepository
import ie.dublinmapper.service.jcdecaux.JcDecauxApi
import ie.dublinmapper.service.jcdecaux.StationJson
import ie.dublinmapper.util.InternetManager
import ie.dublinmapper.util.StringProvider
import ma.glasnost.orika.MapperFacade
import javax.inject.Named
import javax.inject.Singleton

@Module
class DublinBikesRepositoryModule {

    @Provides
    @Singleton
    fun dublinBikesDockRepository(
        api: JcDecauxApi,
        localResource: DublinBikesDockLocalResource,
        persisterDao: PersisterDao,
        internetManager: InternetManager,
        stringProvider: StringProvider,
        mapper: MapperFacade,
        @Named("SHORT_TERM") memoryPolicy: MemoryPolicy
    ): Repository<DublinBikesDock> {
        val fetcher = DublinBikesDockFetcher(
            api,
            stringProvider.jcDecauxApiKey(),
            stringProvider.jcDecauxContract()
        )
        val persister = DublinBikesDockPersister(localResource, mapper, memoryPolicy, persisterDao, internetManager)
        val store = StoreRoom.from(fetcher, persister, StalePolicy.REFRESH_ON_STALE, memoryPolicy)
        return DublinBikesDockRepository(store)
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
        stringProvider: StringProvider,
        mapper: MapperFacade,
        @Named("SHORT_TERM") memoryPolicy: MemoryPolicy
    ): Repository<DublinBikesLiveData> {
        val fetcher = DublinBikesLiveDataFetcher(
            api,
            stringProvider.jcDecauxApiKey(),
            stringProvider.jcDecauxContract()
        )
        val store = StoreBuilder.parsedWithKey<String, StationJson, DublinBikesLiveData>()
            .fetcher(fetcher)
            .parser { liveData -> mapper.map(liveData, DublinBikesLiveData::class.java) }
            .memoryPolicy(memoryPolicy)
            .open()
        return DublinBikesLiveDataRepository(store)
    }

}

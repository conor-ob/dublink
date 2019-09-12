package ie.dublinmapper.repository.luas

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import com.nytimes.android.external.store3.base.impl.StalePolicy
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import dagger.Module
import dagger.Provides
import ie.dublinmapper.datamodel.TxRunner
import ie.dublinmapper.datamodel.aircoach.AircoachStopLocalResource
import ie.dublinmapper.datamodel.favourite.FavouriteDao
import ie.dublinmapper.datamodel.luas.LuasStopDao
import ie.dublinmapper.datamodel.luas.LuasStopLocalResource
import ie.dublinmapper.datamodel.luas.LuasStopLocationDao
import ie.dublinmapper.datamodel.luas.LuasStopServiceDao
import ie.dublinmapper.datamodel.persister.PersisterDao
import ie.dublinmapper.domain.model.LuasLiveData
import ie.dublinmapper.domain.model.LuasStop
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.repository.luas.livedata.LuasLiveDataFetcher
import ie.dublinmapper.repository.luas.livedata.LuasLiveDataRepository
import ie.dublinmapper.repository.luas.stops.LuasStopFetcher
import ie.dublinmapper.repository.luas.stops.LuasStopPersister
import ie.dublinmapper.repository.luas.stops.LuasStopRepository
import ie.dublinmapper.service.rtpi.RtpiApi
import ie.dublinmapper.service.rtpi.RtpiRealTimeBusInformationJson
import ie.dublinmapper.util.InternetManager
import ie.dublinmapper.util.StringProvider
import ma.glasnost.orika.MapperFacade
import javax.inject.Named
import javax.inject.Singleton

@Module
class LuasRepositoryModule {

    @Provides
    @Singleton
    fun luasStopRepository(
        api: RtpiApi,
        localResource: LuasStopLocalResource,
        persisterDao: PersisterDao,
        internetManager: InternetManager,
        stringProvider: StringProvider,
        mapper: MapperFacade,
        @Named("LONG_TERM") memoryPolicy: MemoryPolicy
    ): Repository<LuasStop> {
        val fetcher = LuasStopFetcher(
            api,
            stringProvider.rtpiOperatorLuas(),
            stringProvider.rtpiFormat()
        )
        val persister = LuasStopPersister(localResource, mapper, memoryPolicy, persisterDao, internetManager)
        val store = StoreRoom.from(fetcher, persister, StalePolicy.REFRESH_ON_STALE, memoryPolicy)
        return LuasStopRepository(store)
    }

    @Provides
    @Singleton
    fun luasRealTimeDataRepository(
        api: RtpiApi,
        stringProvider: StringProvider,
        mapper: MapperFacade,
        @Named("SHORT_TERM") memoryPolicy: MemoryPolicy
    ): Repository<LuasLiveData> {
        val fetcher = LuasLiveDataFetcher(
            api,
            stringProvider.rtpiOperatorLuas(),
            stringProvider.rtpiFormat()
        )
        val store = StoreBuilder.parsedWithKey<String, List<RtpiRealTimeBusInformationJson>, List<LuasLiveData>>()
            .fetcher(fetcher)
            .parser { liveData -> mapper.mapAsList(liveData, LuasLiveData::class.java) }
            .memoryPolicy(memoryPolicy)
            .open()
        return LuasLiveDataRepository(store)
    }

}

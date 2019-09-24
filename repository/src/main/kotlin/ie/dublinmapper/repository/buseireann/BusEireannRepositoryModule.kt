package ie.dublinmapper.repository.buseireann

import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import com.nytimes.android.external.store3.base.impl.StalePolicy
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import dagger.Module
import dagger.Provides
import ie.dublinmapper.datamodel.TxRunner
import ie.dublinmapper.datamodel.aircoach.AircoachStopLocalResource
import ie.dublinmapper.datamodel.buseireann.BusEireannStopDao
import ie.dublinmapper.datamodel.buseireann.BusEireannStopLocalResource
import ie.dublinmapper.datamodel.buseireann.BusEireannStopLocationDao
import ie.dublinmapper.datamodel.buseireann.BusEireannStopServiceDao
import ie.dublinmapper.datamodel.favourite.FavouriteDao
import ie.dublinmapper.datamodel.persister.PersisterDao
import ie.dublinmapper.domain.model.BusEireannLiveData
import ie.dublinmapper.domain.model.BusEireannStop
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.repository.buseireann.livedata.BusEireannLiveDataRepository
import ie.dublinmapper.repository.buseireann.stops.BusEireannStopRepository
import ie.dublinmapper.repository.buseireann.stops.BusEireannStopPersister
import ie.dublinmapper.repository.luas.livedata.LuasLiveDataFetcher
import ie.dublinmapper.service.rtpi.RtpiApi
import ie.dublinmapper.service.rtpi.RtpiBusStopInformationJson
import ie.dublinmapper.service.rtpi.RtpiRealTimeBusInformationJson
import ie.dublinmapper.util.InternetManager
import ie.dublinmapper.util.Service
import ie.dublinmapper.util.StringProvider
import ma.glasnost.orika.MapperFacade
import javax.inject.Named
import javax.inject.Singleton

@Module
class BusEireannRepositoryModule {

    @Provides
    @Singleton
    fun busEireannStopRepository(
        api: RtpiApi,
        localResource: BusEireannStopLocalResource,
        persisterDao: PersisterDao,
        stringProvider: StringProvider,
        internetManager: InternetManager,
        mapper: MapperFacade,
        @Named("LONG_TERM") memoryPolicy: MemoryPolicy
    ): Repository<BusEireannStop> {
        val fetcher = Fetcher<List<RtpiBusStopInformationJson>, Service> {
            api.busStopInformation(stringProvider.rtpiOperatorBusEireann(), stringProvider.rtpiFormat())
                .map { it.results }
        }
        val persister = BusEireannStopPersister(localResource, mapper, memoryPolicy, persisterDao, internetManager)
        val store = StoreRoom.from(fetcher, persister, StalePolicy.REFRESH_ON_STALE, memoryPolicy)
        return BusEireannStopRepository(store)
    }

    @Provides
    @Singleton
    fun luasRealTimeDataRepository(
        api: RtpiApi,
        stringProvider: StringProvider,
        mapper: MapperFacade,
        @Named("SHORT_TERM") memoryPolicy: MemoryPolicy
    ): Repository<BusEireannLiveData> {
        val fetcher = LuasLiveDataFetcher(
            api,
            stringProvider.rtpiOperatorBusEireann(),
            stringProvider.rtpiFormat()
        )
        val store = StoreBuilder.parsedWithKey<String, List<RtpiRealTimeBusInformationJson>, List<BusEireannLiveData>>()
            .fetcher(fetcher)
            .parser { liveData -> mapper.mapAsList(liveData, BusEireannLiveData::class.java) }
            .memoryPolicy(memoryPolicy)
            .open()
        return BusEireannLiveDataRepository(store)
    }

}

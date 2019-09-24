package ie.dublinmapper.repository.dublinbus

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import com.nytimes.android.external.store3.base.impl.StalePolicy
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import dagger.Module
import dagger.Provides
import ie.dublinmapper.datamodel.TxRunner
import ie.dublinmapper.datamodel.aircoach.AircoachStopLocalResource
import ie.dublinmapper.datamodel.dublinbus.DublinBusStopDao
import ie.dublinmapper.datamodel.dublinbus.DublinBusStopLocalResource
import ie.dublinmapper.datamodel.dublinbus.DublinBusStopLocationDao
import ie.dublinmapper.datamodel.dublinbus.DublinBusStopServiceDao
import ie.dublinmapper.datamodel.favourite.FavouriteDao
import ie.dublinmapper.datamodel.persister.PersisterDao
import ie.dublinmapper.domain.model.DublinBusLiveData
import ie.dublinmapper.domain.model.DublinBusStop
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.repository.dublinbus.livedata.DublinBusLiveDataFetcher
import ie.dublinmapper.repository.dublinbus.livedata.DublinBusLiveDataRepository
import ie.dublinmapper.repository.dublinbus.stops.DublinBusStopFetcher
import ie.dublinmapper.repository.dublinbus.stops.DublinBusStopPersister
import ie.dublinmapper.repository.dublinbus.stops.DublinBusStopRepository
import ie.dublinmapper.service.dublinbus.DublinBusApi
import ie.dublinmapper.service.rtpi.RtpiApi
import ie.dublinmapper.service.rtpi.RtpiRealTimeBusInformationJson
import ie.dublinmapper.util.InternetManager
import ie.dublinmapper.util.StringProvider
import ie.dublinmapper.util.RxScheduler
import ma.glasnost.orika.MapperFacade
import javax.inject.Named
import javax.inject.Singleton

@Module
class DublinBusRepositoryModule {

    @Provides
    @Singleton
    fun dublinBusStopRepository(
        dublinBusApi: DublinBusApi,
        rtpiApi: RtpiApi,
        localResource: DublinBusStopLocalResource,
        persisterDao: PersisterDao,
        internetManager: InternetManager,
        stringProvider: StringProvider,
        scheduler: RxScheduler,
        mapper: MapperFacade,
        @Named("LONG_TERM") memoryPolicy: MemoryPolicy
    ): Repository<DublinBusStop> {
        val fetcher = DublinBusStopFetcher(
            dublinBusApi,
            rtpiApi,
            stringProvider.rtpiOperatorDublinBus(),
            stringProvider.rtpiOperatorGoAhead(),
            stringProvider.rtpiFormat(),
            scheduler
        )
        val persister = DublinBusStopPersister(localResource, mapper, memoryPolicy, persisterDao, internetManager)
        val store = StoreRoom.from(fetcher, persister, StalePolicy.REFRESH_ON_STALE, memoryPolicy)
        return DublinBusStopRepository(store)
    }

    @Provides
    @Singleton
    fun dublinBusLiveDataRepository(
        dublinBusApi: DublinBusApi,
        api: RtpiApi,
        stringProvider: StringProvider,
        mapper: MapperFacade,
        @Named("SHORT_TERM") memoryPolicy: MemoryPolicy
    ): Repository<DublinBusLiveData> {
        val fetcher = DublinBusLiveDataFetcher(
            dublinBusApi,
            api,
            stringProvider.rtpiOperatorDublinBus(),
            stringProvider.rtpiOperatorGoAhead(),
            stringProvider.rtpiFormat()
        )
        val store = StoreBuilder.parsedWithKey<String, List<RtpiRealTimeBusInformationJson>, List<DublinBusLiveData>>()
            .fetcher(fetcher)
            .parser { liveData -> mapper.mapAsList(liveData, DublinBusLiveData::class.java).sortedBy { it.dueTime.first().minutes } }
            .memoryPolicy(memoryPolicy)
            .open()
        return DublinBusLiveDataRepository(store)
    }

}

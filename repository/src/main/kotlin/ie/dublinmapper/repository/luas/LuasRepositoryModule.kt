package ie.dublinmapper.repository.luas

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import com.nytimes.android.external.store3.base.impl.StalePolicy
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import dagger.Module
import dagger.Provides
import ie.dublinmapper.data.luas.LuasStopCacheResource
import ie.dublinmapper.data.persister.PersisterDao
import ie.dublinmapper.domain.model.LuasLiveData
import ie.dublinmapper.domain.model.LuasStop
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.repository.luas.livedata.LuasLiveDataFetcher
import ie.dublinmapper.repository.luas.livedata.LuasLiveDataMapper
import ie.dublinmapper.repository.luas.livedata.LuasLiveDataRepository
import ie.dublinmapper.repository.luas.stops.LuasStopFetcher
import ie.dublinmapper.repository.luas.stops.LuasStopPersister
import ie.dublinmapper.repository.luas.stops.LuasStopRoomRepository
import ie.dublinmapper.service.rtpi.RtpiApi
import ie.dublinmapper.service.rtpi.RtpiRealTimeBusInformationJson
import ie.dublinmapper.util.InternetManager
import ie.dublinmapper.util.StringProvider
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class LuasRepositoryModule {

    private val shortTermMemoryPolicy: MemoryPolicy by lazy { newMemoryPolicy(30, TimeUnit.SECONDS) }
    private val midTermMemoryPolicy: MemoryPolicy by lazy { newMemoryPolicy(3, TimeUnit.HOURS) }
    private val longTermMemoryPolicy: MemoryPolicy by lazy { newMemoryPolicy(7, TimeUnit.DAYS) }

    private fun newMemoryPolicy(value: Long, timeUnit: TimeUnit): MemoryPolicy {
        return MemoryPolicy.builder()
            .setExpireAfterWrite(value)
            .setExpireAfterTimeUnit(timeUnit)
            .build()
    }

    @Provides
    @Singleton
    fun luasStopRepository(
        api: RtpiApi,
        cacheResource: LuasStopCacheResource,
        persisterDao: PersisterDao,
        internetManager: InternetManager,
        stringProvider: StringProvider
    ): Repository<LuasStop> {
        val fetcher = LuasStopFetcher(
            api,
            stringProvider.rtpiOperatorLuas(),
            stringProvider.rtpiFormat()
        )
        val persister = LuasStopPersister(cacheResource, longTermMemoryPolicy, persisterDao, internetManager)
        val store = StoreRoom.from(fetcher, persister, StalePolicy.REFRESH_ON_STALE, longTermMemoryPolicy)
        return LuasStopRoomRepository(store)
    }

    @Provides
    @Singleton
    fun luasRealTimeDataRepository(
        api: RtpiApi,
        stringProvider: StringProvider
    ): Repository<LuasLiveData> {
        val fetcher = LuasLiveDataFetcher(
            api,
            stringProvider.rtpiOperatorLuas(),
            stringProvider.rtpiFormat()
        )
        val store = StoreBuilder.parsedWithKey<String, List<RtpiRealTimeBusInformationJson>, List<LuasLiveData>>()
            .fetcher(fetcher)
            .parser { liveData -> LuasLiveDataMapper.map(liveData) }
            .open()
        return LuasLiveDataRepository(store)
    }

}

package ie.dublinmapper.repository.buseireann

import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import com.nytimes.android.external.store3.base.impl.StalePolicy
import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import dagger.Module
import dagger.Provides
import ie.dublinmapper.data.buseireann.BusEireannStopCacheResource
import ie.dublinmapper.data.persister.PersisterDao
import ie.dublinmapper.domain.model.BusEireannStop
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.repository.buseireann.stops.BusEireannStopRepository
import ie.dublinmapper.repository.buseireann.stops.BusEireannStopPersister
import ie.dublinmapper.service.rtpi.RtpiApi
import ie.dublinmapper.service.rtpi.RtpiBusStopInformationJson
import ie.dublinmapper.util.InternetManager
import ie.dublinmapper.util.StringProvider
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class BusEireannRepositoryModule {

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
    fun busEireannStopRepository(
        api: RtpiApi,
        cacheResource: BusEireannStopCacheResource,
        persisterDao: PersisterDao,
        stringProvider: StringProvider,
        internetManager: InternetManager
    ): Repository<BusEireannStop> {
        val fetcher = Fetcher<List<RtpiBusStopInformationJson>, String> {
            api.busStopInformation(stringProvider.rtpiOperatorBusEireann(), stringProvider.rtpiFormat())
                .map { it.results }
        }
        val persister = BusEireannStopPersister(cacheResource, longTermMemoryPolicy, persisterDao, internetManager)
        val store = StoreRoom.from(fetcher, persister, StalePolicy.REFRESH_ON_STALE, longTermMemoryPolicy)
        return BusEireannStopRepository(store)
    }

}

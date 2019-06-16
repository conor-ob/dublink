package ie.dublinmapper.repository.aircoach

import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import com.nytimes.android.external.store3.base.impl.StalePolicy
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import dagger.Module
import dagger.Provides
import ie.dublinmapper.data.aircoach.AircoachStopCacheResource
import ie.dublinmapper.data.persister.PersisterDao
import ie.dublinmapper.domain.model.AircoachLiveData
import ie.dublinmapper.domain.model.AircoachStop
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.repository.aircoach.livedata.AircoachLiveDataMapper
import ie.dublinmapper.repository.aircoach.livedata.AircoachLiveDataRepository
import ie.dublinmapper.repository.aircoach.stops.AircoachStopPersister
import ie.dublinmapper.repository.aircoach.stops.AircoachStopRepository
import ie.dublinmapper.service.aircoach.AircoachResource
import ie.dublinmapper.service.aircoach.AircoachStopJson
import ie.dublinmapper.service.aircoach.ServiceResponseJson
import ie.dublinmapper.util.InternetManager
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class AircoachRepositoryModule {

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
    fun aircoachStopRepository(
        resource: AircoachResource,
        cacheResource: AircoachStopCacheResource,
        persisterDao: PersisterDao,
        internetManager: InternetManager
    ): Repository<AircoachStop> {
        val fetcher = Fetcher<List<AircoachStopJson>, String> { resource.getStops() }
        val persister = AircoachStopPersister(cacheResource, longTermMemoryPolicy, persisterDao, internetManager)
        val store = StoreRoom.from(fetcher, persister, StalePolicy.REFRESH_ON_STALE, longTermMemoryPolicy)
        return AircoachStopRepository(store)
    }

    @Provides
    @Singleton
    fun aircoachLiveDataRepository(
        resource: AircoachResource
    ): Repository<AircoachLiveData> {
        val store = StoreBuilder.parsedWithKey<String, ServiceResponseJson, List<AircoachLiveData>>()
            .fetcher { stopId -> resource.getLiveData(stopId) }
            .parser { liveData -> AircoachLiveDataMapper.map(liveData.services).filter { it.dueTime.first().minutes >= 0 } } //TODO
            .open()
        return AircoachLiveDataRepository(store)
    }

}

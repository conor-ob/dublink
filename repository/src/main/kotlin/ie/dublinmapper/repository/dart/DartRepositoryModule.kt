package ie.dublinmapper.repository.dart

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import com.nytimes.android.external.store3.base.impl.StalePolicy
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import dagger.Module
import dagger.Provides
import ie.dublinmapper.data.dart.DartStationCacheResource
import ie.dublinmapper.data.persister.PersisterDao
import ie.dublinmapper.domain.model.DartLiveData
import ie.dublinmapper.domain.model.DartStation
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.repository.dart.livedata.DartLiveDataFetcher
import ie.dublinmapper.repository.dart.livedata.DartLiveDataMapper
import ie.dublinmapper.repository.dart.livedata.DartLiveDataRepository
import ie.dublinmapper.repository.dart.stations.DartStationFetcher
import ie.dublinmapper.repository.dart.stations.DartStationPersister
import ie.dublinmapper.repository.dart.stations.DartStationRepository
import ie.dublinmapper.service.irishrail.IrishRailApi
import ie.dublinmapper.service.irishrail.IrishRailStationDataXml
import ie.dublinmapper.util.InternetManager
import ie.dublinmapper.util.StringProvider
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class DartRepositoryModule {

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
    fun dartStationRepository(
        api: IrishRailApi,
        cacheResource: DartStationCacheResource,
        persisterDao: PersisterDao,
        internetManager: InternetManager,
        stringProvider: StringProvider
    ): Repository<DartStation> {
        val fetcher = DartStationFetcher(
            api,
            stringProvider.irishRailApiDartStationType()
        )
        val persister = DartStationPersister(cacheResource, longTermMemoryPolicy, persisterDao, internetManager)
        val store = StoreRoom.from(fetcher, persister, StalePolicy.REFRESH_ON_STALE, longTermMemoryPolicy)
        return DartStationRepository(store)
    }

    @Provides
    @Singleton
    fun dartLiveDataRepository(
        api: IrishRailApi
    ): Repository<DartLiveData> {
        val fetcher = DartLiveDataFetcher(api)
        val store = StoreBuilder.parsedWithKey<String, List<IrishRailStationDataXml>, List<DartLiveData>>()
            .fetcher(fetcher)
            .parser { liveData -> DartLiveDataMapper.map(liveData).sortedBy { it.dueTime[0].minutes } } //TODO do the sorting somewhere else
            .memoryPolicy(shortTermMemoryPolicy)
            .open()
        return DartLiveDataRepository(store)
    }

}

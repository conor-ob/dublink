package ie.dublinmapper.repository.swordsexpress

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import com.nytimes.android.external.store3.base.impl.StalePolicy
import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import dagger.Module
import dagger.Provides
import ie.dublinmapper.data.persister.PersisterDao
import ie.dublinmapper.data.swordsexpress.SwordsExpressStopCacheResource
import ie.dublinmapper.domain.model.SwordsExpressStop
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.repository.swordsexpress.stops.SwordsExpressStopFetcher
import ie.dublinmapper.repository.swordsexpress.stops.SwordsExpressStopPersister
import ie.dublinmapper.repository.swordsexpress.stops.SwordsExpressStopRepository
import ie.dublinmapper.service.github.GithubApi
import ie.dublinmapper.util.InternetManager
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class SwordsExpressRepositoryModule {

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
    fun swordsExpressStopRepository(
        api: GithubApi,
        cacheResource: SwordsExpressStopCacheResource,
        persisterDao: PersisterDao,
        internetManager: InternetManager
    ): Repository<SwordsExpressStop> {
        val fetcher = SwordsExpressStopFetcher(api)
        val persister = SwordsExpressStopPersister(cacheResource, longTermMemoryPolicy, persisterDao, internetManager)
        val store = StoreRoom.from(fetcher, persister, StalePolicy.REFRESH_ON_STALE, longTermMemoryPolicy)
        return SwordsExpressStopRepository(store)
    }

}

package ie.dublinmapper.repository.swordsexpress

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import com.nytimes.android.external.store3.base.impl.StalePolicy
import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import dagger.Module
import dagger.Provides
import ie.dublinmapper.datamodel.persister.PersisterDao
import ie.dublinmapper.datamodel.swordsexpress.SwordsExpressStopLocalResource
import ie.dublinmapper.domain.model.SwordsExpressStop
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.repository.swordsexpress.stops.SwordsExpressStopFetcher
import ie.dublinmapper.repository.swordsexpress.stops.SwordsExpressStopPersister
import ie.dublinmapper.repository.swordsexpress.stops.SwordsExpressStopRepository
import ie.dublinmapper.service.github.GithubApi
import ie.dublinmapper.util.InternetManager
import javax.inject.Named
import javax.inject.Singleton

@Module
class SwordsExpressRepositoryModule {

    @Provides
    @Singleton
    fun swordsExpressStopRepository(
        api: GithubApi,
        localResource: SwordsExpressStopLocalResource,
        persisterDao: PersisterDao,
        internetManager: InternetManager,
        @Named("LONG_TERM") memoryPolicy: MemoryPolicy
    ): Repository<SwordsExpressStop> {
        val fetcher = SwordsExpressStopFetcher(api)
        val persister = SwordsExpressStopPersister(localResource, memoryPolicy, persisterDao, internetManager)
        val store = StoreRoom.from(fetcher, persister, StalePolicy.REFRESH_ON_STALE, memoryPolicy)
        return SwordsExpressStopRepository(store)
    }

}

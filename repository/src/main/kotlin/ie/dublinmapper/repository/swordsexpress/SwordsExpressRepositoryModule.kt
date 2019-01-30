package ie.dublinmapper.repository.swordsexpress

import com.nytimes.android.external.store3.base.impl.StoreBuilder
import dagger.Module
import dagger.Provides
import ie.dublinmapper.domain.model.SwordsExpressStop
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.service.swordsexpress.SwordsExpressApi
import ie.dublinmapper.service.swordsexpress.SwordsExpressStopJson
import javax.inject.Singleton

@Module
class SwordsExpressRepositoryModule {

    @Provides
    @Singleton
    fun swordsExpressStopRepository(
        api: SwordsExpressApi
    ): Repository<SwordsExpressStop> {
        val fetcher = SwordsExpressStopFetcher(api)
        val store = StoreBuilder.parsedWithKey<String, List<SwordsExpressStopJson>, List<SwordsExpressStop>>()
            .fetcher(fetcher)
            .parser { stops -> SwordsExpressStopMapper.map(stops) }
            .open()
        return SwordsExpressStopRepository(store)
    }

}

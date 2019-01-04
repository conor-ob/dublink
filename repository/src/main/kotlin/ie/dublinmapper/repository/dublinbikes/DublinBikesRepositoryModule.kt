package ie.dublinmapper.repository.dublinbikes

import com.nytimes.android.external.store3.base.impl.StoreBuilder
import dagger.Module
import dagger.Provides
import ie.dublinmapper.domain.dublinbikes.DublinBikesDock
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.service.jcdecaux.JcdecauxApi
import ie.dublinmapper.service.jcdecaux.StationJson
import javax.inject.Singleton

@Module
class DublinBikesRepositoryModule {

    @Provides
    @Singleton
    fun dublinBikesDockRepository(
        api: JcdecauxApi
    ): Repository<DublinBikesDock> {
        val fetcher = DublinBikesDockFetcher(api)
        val store = StoreBuilder.parsedWithKey<String, List<StationJson>, List<DublinBikesDock>>()
            .fetcher(fetcher)
            .open()
        return DublinBikeDockRepository(store)
    }

}

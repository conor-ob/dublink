package ie.dublinmapper.repository.dublinbikes

import com.nytimes.android.external.store3.base.impl.StoreBuilder
import dagger.Module
import dagger.Provides
import ie.dublinmapper.domain.model.DublinBikesDock
import ie.dublinmapper.domain.model.DublinBikesLiveData
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.service.jcdecaux.JcDecauxApi
import ie.dublinmapper.service.jcdecaux.StationJson
import ie.dublinmapper.util.StringProvider
import javax.inject.Singleton

@Module
class DublinBikesRepositoryModule {

    @Provides
    @Singleton
    fun dublinBikesDockRepository(
        api: JcDecauxApi,
        stringProvider: StringProvider
    ): Repository<DublinBikesDock> {
        val fetcher = DublinBikesDockFetcher(api, stringProvider.jcDecauxApiKey(), stringProvider.jcDecauxContract())
        val store = StoreBuilder.parsedWithKey<String, List<StationJson>, List<DublinBikesDock>>()
            .fetcher(fetcher)
            .parser { docks -> DublinBikesDockMapper.map(docks) }
            .open()
        return DublinBikesDockRepository(store)
    }

    @Provides
    @Singleton
    fun dublinBikesRealTimeDataRepository(
        api: JcDecauxApi,
        stringProvider: StringProvider
    ): Repository<DublinBikesLiveData> {
        val fetcher = DublinBikesLiveDataFetcher(api, stringProvider.jcDecauxApiKey(), stringProvider.jcDecauxContract())
        val store = StoreBuilder.parsedWithKey<String, StationJson, DublinBikesLiveData>()
            .fetcher(fetcher)
            .parser { liveData -> DublinBikesLiveDataMapper.map(liveData) }
            .open()
        return DublinBikesLiveDataRepository(store)
    }

}

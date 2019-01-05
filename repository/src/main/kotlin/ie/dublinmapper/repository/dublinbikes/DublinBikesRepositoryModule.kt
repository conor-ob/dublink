package ie.dublinmapper.repository.dublinbikes

import com.nytimes.android.external.store3.base.impl.StoreBuilder
import dagger.Module
import dagger.Provides
import ie.dublinmapper.domain.dublinbikes.DublinBikesDock
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.service.jcdecaux.JcdecauxApi
import ie.dublinmapper.service.jcdecaux.StationJson
import javax.inject.Named
import javax.inject.Singleton

@Module
class DublinBikesRepositoryModule {

    @Provides
    @Singleton
    fun dublinBikesDockRepository(
        api: JcdecauxApi,
        @Named("jcdecaux_contract") jcdecauxContract: String,
        @Named("api.jcdecaux.API_KEY") jcdecauxApiKey: String
    ): Repository<DublinBikesDock> {
        val fetcher = DublinBikesDockFetcher(api, jcdecauxApiKey, jcdecauxContract)
        val store = StoreBuilder.parsedWithKey<String, List<StationJson>, List<DublinBikesDock>>()
            .fetcher(fetcher)
            .parser { json -> json.map { DublinBikesDock(
                it.number.toString(),
                it.address!!,
                it.bikeStands!!,
                it.availableBikeStands!!,
                it.availableBikes!!
            ) } }
            .open()
        return DublinBikeDockRepository(store)
    }

}

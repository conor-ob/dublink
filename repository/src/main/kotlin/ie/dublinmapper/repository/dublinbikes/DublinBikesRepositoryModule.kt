package ie.dublinmapper.repository.dublinbikes

import com.nytimes.android.external.store3.base.impl.StoreBuilder
import dagger.Module
import dagger.Provides
import ie.dublinmapper.domain.model.dublinbikes.DublinBikesDock
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.service.jcdecaux.JcdecauxApi
import ie.dublinmapper.service.jcdecaux.StationJson
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.Operator
import ie.dublinmapper.util.StringProvider
import javax.inject.Singleton

@Module
class DublinBikesRepositoryModule {

    @Provides
    @Singleton
    fun dublinBikesDockRepository(
        api: JcdecauxApi,
        stringProvider: StringProvider
    ): Repository<DublinBikesDock> {
        val fetcher = DublinBikesDockFetcher(api, stringProvider.jcdecauxApiKey(), stringProvider.jcdecauxContract())
        val store = StoreBuilder.parsedWithKey<String, List<StationJson>, List<DublinBikesDock>>()
            .fetcher(fetcher)
            .parser { json -> json.map { DublinBikesDock(
                it.number.toString(),
                it.address!!,
                Coordinate(it.position!!.lat!!, it.position!!.lng!!),
                Operator.DUBLIN_BIKES,
                it.bikeStands!!,
                it.availableBikeStands!!,
                it.availableBikes!!
            ) } }
            .open()
        return DublinBikeDockRepository(store)
    }

}

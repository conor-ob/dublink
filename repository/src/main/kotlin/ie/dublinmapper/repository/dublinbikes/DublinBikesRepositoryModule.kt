package ie.dublinmapper.repository.dublinbikes

import com.nytimes.android.external.store3.base.impl.StoreBuilder
import dagger.Module
import dagger.Provides
import ie.dublinmapper.domain.model.dublinbikes.DublinBikesDock
import ie.dublinmapper.domain.model.dublinbikes.DublinBikesRealTimeData
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.service.jcdecaux.JcDecauxApi
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
        api: JcDecauxApi,
        stringProvider: StringProvider
    ): Repository<DublinBikesDock> {
        val fetcher = DublinBikesDockFetcher(api, stringProvider.jcDecauxApiKey(), stringProvider.jcDecauxContract())
        val store = StoreBuilder.parsedWithKey<String, List<StationJson>, List<DublinBikesDock>>()
            .fetcher(fetcher)
            .parser { json -> json.map { DublinBikesDock(
                it.number.toString(),
                it.address!!,
                Coordinate(it.position!!.lat!!, it.position!!.lng!!),
                Operator.DUBLIN_BIKES,
                it.availableBikes!!.toString()
            ) } }
            .open()
        return DublinBikeDockRepository(store)
    }

    @Provides
    @Singleton
    fun dublinBikesRealTimeDataRepository(
        api: JcDecauxApi,
        stringProvider: StringProvider
    ): Repository<DublinBikesRealTimeData> {
        val fetcher = DublinBikesRealTimeDataFetcher(api, stringProvider.jcDecauxApiKey(), stringProvider.jcDecauxContract())
        val store = StoreBuilder.parsedWithKey<String, StationJson, DublinBikesRealTimeData>()
            .fetcher(fetcher)
            .parser { json ->
                DublinBikesRealTimeData()
                TODO()
            }
            .open()
        return DublinBikesRealTimeDataRepository(store)
    }

}

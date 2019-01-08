package ie.dublinmapper.repository.luas

import com.nytimes.android.external.store3.base.impl.StoreBuilder
import dagger.Module
import dagger.Provides
import ie.dublinmapper.domain.model.LuasLiveData
import ie.dublinmapper.domain.model.LuasStop
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.service.rtpi.RtpiApi
import ie.dublinmapper.service.rtpi.RtpiBusStopInformationJson
import ie.dublinmapper.service.rtpi.RtpiRealTimeBusInformationJson
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.StringProvider
import javax.inject.Singleton

@Module
class LuasStopRepositoryModule {

    @Provides
    @Singleton
    fun luasStopRepository(
        api: RtpiApi,
        stringProvider: StringProvider
    ): Repository<LuasStop> {
        val fetcher = LuasStopFetcher(api, stringProvider.rtpiOperatoreLuas(), stringProvider.rtpiFormat())
        val store = StoreBuilder.parsedWithKey<String, List<RtpiBusStopInformationJson>, List<LuasStop>>()
            .fetcher(fetcher)
            .parser { json -> json.map { LuasStop(
                id = it.displayId!!,
                name = it.fullName!!,
                coordinate = Coordinate(it.latitude!!.toDouble(), it.longitude!!.toDouble())
            ) } }
            .open()
        return LuasStopRepository(store)
    }

    @Provides
    @Singleton
    fun luasRealTimeDataRepository(
        api: RtpiApi,
        stringProvider: StringProvider
    ): Repository<LuasLiveData> {
        val fetcher = LuasRealTimeDataFetcher(api, stringProvider.rtpiOperatoreLuas(), stringProvider.rtpiFormat())
        val store = StoreBuilder.parsedWithKey<String, List<RtpiRealTimeBusInformationJson>, List<LuasLiveData>>()
            .fetcher(fetcher)
            .parser { json -> json.map {
                LuasLiveData()
                TODO()
            } }
            .open()
        return LuasRealTimeDataRepository(store)
    }

}

package ie.dublinmapper.repository.dublinbus

import com.nytimes.android.external.store3.base.impl.StoreBuilder
import dagger.Module
import dagger.Provides
import ie.dublinmapper.domain.model.DublinBusStop
import ie.dublinmapper.domain.model.DueTime
import ie.dublinmapper.domain.model.LiveData
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.service.dublinbus.DublinBusApi
import ie.dublinmapper.service.rtpi.RtpiApi
import ie.dublinmapper.service.rtpi.RtpiBusStopInformationJson
import ie.dublinmapper.service.rtpi.RtpiRealTimeBusInformationJson
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.StringProvider
import org.threeten.bp.LocalTime
import javax.inject.Singleton

@Module
class DublinBusRepositoryModule {

    @Provides
    @Singleton
    fun dublinBusStopRepository(
        dublinBusApi: DublinBusApi,
        rtpiApi: RtpiApi,
        stringProvider: StringProvider
    ): Repository<DublinBusStop> {
        val fetcher = DublinBusStopFetcher(dublinBusApi, rtpiApi, stringProvider.rtpiOperatoreDublinBus(), stringProvider.rtpiOperatoreGoAhead(), stringProvider.rtpiFormat())
        val store = StoreBuilder.parsedWithKey<String, List<RtpiBusStopInformationJson>, List<DublinBusStop>>()
            .fetcher(fetcher)
            .parser { json -> json.map { DublinBusStop(
                id = it.displayId!!,
                name = it.fullName!!,
                coordinate = Coordinate(it.latitude!!.toDouble(), it.longitude!!.toDouble())
            ) } }
            .open()
        return DublinBusStopRepository(store)
    }

    @Provides
    @Singleton
    fun dublinBusRealTimeDataRepository(
        dublinBusApi: DublinBusApi,
        api: RtpiApi,
        stringProvider: StringProvider
    ): Repository<LiveData.DublinBus> {
        val fetcher = DublinLiveDataFetcher(dublinBusApi, api, stringProvider.rtpiOperatoreDublinBus(), stringProvider.rtpiOperatoreGoAhead(), stringProvider.rtpiFormat())
        val store = StoreBuilder.parsedWithKey<String, List<RtpiRealTimeBusInformationJson>, List<LiveData.DublinBus>>()
            .fetcher(fetcher)
            .parser { json -> json.map {
                LiveData.DublinBus(DueTime(0L, LocalTime.now()))
                TODO()
            } }
            .open()
        return DublinBusLiveDataRepository(store)
    }

}

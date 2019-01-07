package ie.dublinmapper.repository.dublinbus

import com.nytimes.android.external.store3.base.impl.StoreBuilder
import dagger.Module
import dagger.Provides
import ie.dublinmapper.domain.model.dublinbus.DublinBusStop
import ie.dublinmapper.domain.model.luas.LuasStop
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.service.rtpi.RtpiApi
import ie.dublinmapper.service.rtpi.RtpiBusStopInformationJson
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.Operator
import ie.dublinmapper.util.StringProvider
import javax.inject.Singleton

@Module
class DublinBusRepositoryModule {

    @Provides
    @Singleton
    fun dublinBusStopRepository(
        api: RtpiApi,
        stringProvider: StringProvider
    ): Repository<DublinBusStop> {
        val fetcher = DublinBusStopFetcher(api, stringProvider.rtpiOperatoreDublinBus(), stringProvider.rtpiFormat())
        val store = StoreBuilder.parsedWithKey<String, List<RtpiBusStopInformationJson>, List<DublinBusStop>>()
            .fetcher(fetcher)
            .parser { json -> json.map { DublinBusStop(
                it.displayId!!,
                it.fullName!!,
                Coordinate(it.latitude!!.toDouble(), it.longitude!!.toDouble()),
                Operator.DUBLIN_BUS
            ) } }
            .open()
        return DublinBusStopRepository(store)
    }

}

package ie.dublinmapper.repository.buseireann

import com.nytimes.android.external.store3.base.impl.StoreBuilder
import dagger.Module
import dagger.Provides
import ie.dublinmapper.domain.model.BusEireannStop
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.service.rtpi.RtpiApi
import ie.dublinmapper.service.rtpi.RtpiBusStopInformationJson
import ie.dublinmapper.util.StringProvider
import javax.inject.Singleton

@Module
class BusEireannRepositoryModule {

    @Provides
    @Singleton
    fun busEireannStopRepository(
        api: RtpiApi,
        stringProvider: StringProvider
    ): Repository<BusEireannStop> {
        val store = StoreBuilder.parsedWithKey<String, List<RtpiBusStopInformationJson>, List<BusEireannStop>>()
            .fetcher { api.busStopInformation(
                stringProvider.rtpiOperatorBusEireann(), stringProvider.rtpiFormat()
            ).map { it.results } }
            .parser { stops -> BusEireannStopMapper.map(stops) }
            .open()
        return BusEireannStopRepository(store)
    }

}

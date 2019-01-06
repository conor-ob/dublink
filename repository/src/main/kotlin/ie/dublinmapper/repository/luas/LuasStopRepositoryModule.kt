package ie.dublinmapper.repository.luas

import com.nytimes.android.external.store3.base.impl.StoreBuilder
import dagger.Module
import dagger.Provides
import ie.dublinmapper.domain.model.luas.LuasStop
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.service.rtpi.RtpiApi
import ie.dublinmapper.service.rtpi.RtpiBusStopInformationJson
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.Operator
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
                it.displayId!!,
                it.fullName!!,
                Coordinate(it.latitude!!.toDouble(), it.longitude!!.toDouble()),
                Operator.LUAS
            ) } }
            .open()
        return LuasStopRepository(store)
    }

}

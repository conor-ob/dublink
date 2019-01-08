package ie.dublinmapper.repository.dart

import com.nytimes.android.external.store3.base.impl.StoreBuilder
import dagger.Module
import dagger.Provides
import ie.dublinmapper.domain.model.DartLiveData
import ie.dublinmapper.domain.model.DartStation
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.service.irishrail.IrishRailApi
import ie.dublinmapper.service.irishrail.IrishRailStationDataXml
import ie.dublinmapper.service.irishrail.IrishRailStationXml
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.StringProvider
import javax.inject.Singleton

@Module
class DartRepositoryModule {

    @Provides
    @Singleton
    fun dartStationRepository(
        api: IrishRailApi,
        stringProvider: StringProvider
    ): Repository<DartStation> {
        val fetcher = DartStationFetcher(api, stringProvider.irishRailApiDartStationType())
        val store = StoreBuilder.parsedWithKey<String, List<IrishRailStationXml>, List<DartStation>>()
            .fetcher(fetcher)
            .parser { json -> json.map {
                DartStation(
                    id = it.id!!,
                    name = it.name!!,
                    coordinate = Coordinate(it.latitude!!.toDouble(), it.longitude!!.toDouble())
                )
            } }
            .open()
        return DartStationRepository(store)
    }

    @Provides
    @Singleton
    fun dartRealTimeDataRepository(
        api: IrishRailApi
    ): Repository<DartLiveData> {
        val fetcher = DartRealTimeDataFetcher(api)
        val store = StoreBuilder.parsedWithKey<String, List<IrishRailStationDataXml>, List<DartLiveData>>()
            .fetcher(fetcher)
            .parser { json -> json.map {
                DartLiveData()
                TODO()
            } }
            .open()
        return DartRealTimeDataRepository(store)
    }

}

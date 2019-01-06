package ie.dublinmapper.repository.dart

import com.nytimes.android.external.store3.base.impl.StoreBuilder
import dagger.Module
import dagger.Provides
import ie.dublinmapper.domain.model.DartStation
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.service.irishrail.IrishRailApi
import ie.dublinmapper.service.irishrail.IrishRailStationXml
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.Operator
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
            .parser { json -> json.map { DartStation(
                it.id!!,
                it.name!!,
                Coordinate(it.latitude!!.toDouble(), it.longitude!!.toDouble()),
                Operator.DART
            ) } }
            .open()
        return DartStationRepository(store)
    }

}

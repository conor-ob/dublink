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
            .parser { stations -> DartStationMapper.map(stations) }
            .open()
        return DartStationRepository(store)
    }

    @Provides
    @Singleton
    fun dartLiveDataRepository(
        api: IrishRailApi
    ): Repository<DartLiveData> {
        val fetcher = DartLiveDataFetcher(api)
        val store = StoreBuilder.parsedWithKey<String, List<IrishRailStationDataXml>, List<DartLiveData>>()
            .fetcher(fetcher)
            .parser { liveData -> DartLiveDataMapper.map(liveData).sortedBy { it.dueTime[0].minutes } } //TODO do the sorting somewhere else
            .open()
        return DartLiveDataRepository(store)
    }

}

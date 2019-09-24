package ie.dublinmapper.repository.irishrail

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import com.nytimes.android.external.store3.base.impl.StalePolicy
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import dagger.Module
import dagger.Provides
import ie.dublinmapper.datamodel.TxRunner
import ie.dublinmapper.datamodel.aircoach.AircoachStopLocalResource
import ie.dublinmapper.datamodel.favourite.FavouriteDao
import ie.dublinmapper.datamodel.irishrail.IrishRailStationDao
import ie.dublinmapper.datamodel.irishrail.IrishRailStationLocalResource
import ie.dublinmapper.datamodel.irishrail.IrishRailStationLocationDao
import ie.dublinmapper.datamodel.irishrail.IrishRailStationServiceDao
import ie.dublinmapper.datamodel.persister.PersisterDao
import ie.dublinmapper.domain.model.IrishRailLiveData
import ie.dublinmapper.domain.model.IrishRailStation
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.repository.irishrail.livedata.IrishRailLiveDataFetcher
import ie.dublinmapper.repository.irishrail.livedata.IrishRailLiveDataRepository
import ie.dublinmapper.repository.irishrail.stations.IrishRailStationFetcher
import ie.dublinmapper.repository.irishrail.stations.IrishRailStationPersister
import ie.dublinmapper.repository.irishrail.stations.IrishRailStationRepository
import ie.dublinmapper.service.irishrail.IrishRailApi
import ie.dublinmapper.service.irishrail.IrishRailStationDataXml
import ie.dublinmapper.util.InternetManager
import ie.dublinmapper.util.StringProvider
import ma.glasnost.orika.MapperFacade
import javax.inject.Named
import javax.inject.Singleton

@Module
class IrishRailRepositoryModule {

    @Provides
    @Singleton
    fun irishRailStationRepository(
        api: IrishRailApi,
        localResource: IrishRailStationLocalResource,
        persisterDao: PersisterDao,
        internetManager: InternetManager,
        stringProvider: StringProvider,
        mapper: MapperFacade,
        @Named("LONG_TERM") memoryPolicy: MemoryPolicy
    ): Repository<IrishRailStation> {
        val fetcher = IrishRailStationFetcher(
            api,
            stringProvider.irishRailApiDartStationType()
        )
        val persister = IrishRailStationPersister(localResource, mapper, memoryPolicy, persisterDao, internetManager)
        val store = StoreRoom.from(fetcher, persister, StalePolicy.REFRESH_ON_STALE, memoryPolicy)
        return IrishRailStationRepository(store)
    }

    @Provides
    @Singleton
    fun irishRailLiveDataRepository(
        api: IrishRailApi,
        mapper: MapperFacade,
        @Named("SHORT_TERM") memoryPolicy: MemoryPolicy
    ): Repository<IrishRailLiveData> {
        val fetcher = IrishRailLiveDataFetcher(api)
        val store = StoreBuilder.parsedWithKey<String, List<IrishRailStationDataXml>, List<IrishRailLiveData>>()
            .fetcher(fetcher)
            .parser { liveData ->
                mapper.mapAsList(liveData, IrishRailLiveData::class.java).sortedBy { it.dueTime[0].minutes } //TODO do the sorting somewhere else
            }
            .memoryPolicy(memoryPolicy)
            .open()
        return IrishRailLiveDataRepository(store)
    }

}

package ie.dublinmapper.repository.irishrail

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import com.nytimes.android.external.store3.base.impl.StalePolicy
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import dagger.Module
import dagger.Provides
import ie.dublinmapper.datamodel.irishrail.IrishRailStationLocalResource
import ie.dublinmapper.datamodel.persister.PersisterDao
import ie.dublinmapper.domain.model.IrishRailStation
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.repository.irishrail.livedata.IrishRailLiveDataRepository
import ie.dublinmapper.repository.irishrail.stations.IrishRailStationFetcher
import ie.dublinmapper.repository.irishrail.stations.IrishRailStationPersister
import ie.dublinmapper.repository.irishrail.stations.IrishRailStationRepository
import ie.dublinmapper.service.irishrail.IrishRailApi
import ie.dublinmapper.util.InternetManager
import ie.dublinmapper.util.StringProvider
import io.reactivex.Single
import io.rtpi.api.IrishRailLiveData
import io.rtpi.client.RtpiClient
import ma.glasnost.orika.MapperFacade
import org.threeten.bp.LocalTime
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
        client: RtpiClient,
        @Named("SHORT_TERM") memoryPolicy: MemoryPolicy
    ): Repository<IrishRailLiveData> {
        val store = StoreBuilder.key<String, List<IrishRailLiveData>>()
//            .fetcher { stationId -> client.irishRail().getLiveData(stationId = stationId) }
            .fetcher { stationId -> Single.just(client.irishRail().getLiveData(stationId = stationId)) }
            .memoryPolicy(memoryPolicy)
            .open()
        return IrishRailLiveDataRepository(store)
    }

}

package ie.dublinmapper.repository.luas

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import com.nytimes.android.external.store3.base.impl.StalePolicy
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import dagger.Module
import dagger.Provides
import ie.dublinmapper.datamodel.luas.LuasStopLocalResource
import ie.dublinmapper.datamodel.persister.PersisterDao
import ie.dublinmapper.domain.model.LuasStop
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.repository.luas.livedata.LuasLiveDataRepository
import ie.dublinmapper.repository.luas.stops.LuasStopFetcher
import ie.dublinmapper.repository.luas.stops.LuasStopPersister
import ie.dublinmapper.repository.luas.stops.LuasStopRepository
import ie.dublinmapper.service.rtpi.RtpiApi
import ie.dublinmapper.util.InternetManager
import ie.dublinmapper.util.StringProvider
import io.reactivex.Single
import io.rtpi.api.LuasLiveData
import io.rtpi.client.RtpiClient
import ma.glasnost.orika.MapperFacade
import org.threeten.bp.LocalTime
import javax.inject.Named
import javax.inject.Singleton

@Module
class LuasRepositoryModule {

    @Provides
    @Singleton
    fun luasStopRepository(
        api: RtpiApi,
        localResource: LuasStopLocalResource,
        persisterDao: PersisterDao,
        internetManager: InternetManager,
        stringProvider: StringProvider,
        mapper: MapperFacade,
        @Named("LONG_TERM") memoryPolicy: MemoryPolicy
    ): Repository<LuasStop> {
        val fetcher = LuasStopFetcher(
            api,
            stringProvider.rtpiOperatorLuas(),
            stringProvider.rtpiFormat()
        )
        val persister = LuasStopPersister(localResource, mapper, memoryPolicy, persisterDao, internetManager)
        val store = StoreRoom.from(fetcher, persister, StalePolicy.REFRESH_ON_STALE, memoryPolicy)
        return LuasStopRepository(store)
    }

    @Provides
    @Singleton
    fun luasRealTimeDataRepository(
        client: RtpiClient,
        @Named("SHORT_TERM") memoryPolicy: MemoryPolicy
    ): Repository<LuasLiveData> {
        val store = StoreBuilder.key<String, List<LuasLiveData>>()
            .fetcher { stopId -> client.luas().getLiveData(stopId = stopId) }
//            .fetcher { stopId -> Single.just(client.luas().getLiveData(stopId = stopId)) }
            .memoryPolicy(memoryPolicy)
            .open()
        return LuasLiveDataRepository(store)
    }

}

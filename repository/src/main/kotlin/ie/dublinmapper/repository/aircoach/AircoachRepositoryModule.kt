package ie.dublinmapper.repository.aircoach

import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import com.nytimes.android.external.store3.base.impl.StalePolicy
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import dagger.Module
import dagger.Provides
import ie.dublinmapper.datamodel.aircoach.AircoachStopLocalResource
import ie.dublinmapper.datamodel.persister.PersisterDao
import ie.dublinmapper.domain.model.DetailedAircoachStop
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.repository.aircoach.livedata.AircoachLiveDataRepository
import ie.dublinmapper.repository.aircoach.stops.AircoachStopPersister
import ie.dublinmapper.repository.aircoach.stops.AircoachStopRepository
import ie.dublinmapper.service.aircoach.AircoachStopRemoteResource
import ie.dublinmapper.service.aircoach.AircoachStopJson
import ie.dublinmapper.util.InternetManager
import io.reactivex.Single
import io.rtpi.api.AircoachLiveData
import io.rtpi.api.AircoachStop
import io.rtpi.api.Service
import io.rtpi.client.RtpiClient
import ma.glasnost.orika.MapperFacade
import javax.inject.Named
import javax.inject.Singleton

@Module
class AircoachRepositoryModule {

    @Provides
    @Singleton
    fun aircoachStopRepository(
        client: RtpiClient,
        localResource: AircoachStopLocalResource,
        persisterDao: PersisterDao,
        internetManager: InternetManager,
        mapper: MapperFacade,
        @Named("LONG_TERM") memoryPolicy: MemoryPolicy
    ): Repository<DetailedAircoachStop> {
        val fetcher = Fetcher<List<AircoachStop>, Service> { Single.just(client.aircoach().getStops()) }
        val persister = AircoachStopPersister(localResource, mapper, memoryPolicy, persisterDao, internetManager)
        val store = StoreRoom.from(fetcher, persister, StalePolicy.REFRESH_ON_STALE, memoryPolicy)
        return AircoachStopRepository(store)
    }

    @Provides
    @Singleton
    fun aircoachLiveDataRepository(
        client: RtpiClient,
        @Named("SHORT_TERM") memoryPolicy: MemoryPolicy
    ): Repository<AircoachLiveData> {
        val store = StoreBuilder.key<String, List<AircoachLiveData>>()
//            .fetcher { stopId -> client.aircoach().getLiveData(stopId = stopId) }
            .fetcher { stopId -> Single.just(client.aircoach().getLiveData(stopId = stopId)) }
            .memoryPolicy(memoryPolicy)
            .open()
        return AircoachLiveDataRepository(store)
    }

}

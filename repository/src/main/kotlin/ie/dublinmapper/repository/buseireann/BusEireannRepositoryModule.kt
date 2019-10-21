package ie.dublinmapper.repository.buseireann

import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import com.nytimes.android.external.store3.base.impl.StalePolicy
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import dagger.Module
import dagger.Provides
import ie.dublinmapper.datamodel.buseireann.BusEireannStopLocalResource
import ie.dublinmapper.datamodel.persister.PersisterDao
import ie.dublinmapper.domain.model.DetailedBusEireannStop
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.repository.buseireann.livedata.BusEireannLiveDataRepository
import ie.dublinmapper.repository.buseireann.stops.BusEireannStopRepository
import ie.dublinmapper.repository.buseireann.stops.BusEireannStopPersister
import ie.dublinmapper.util.InternetManager
import ie.dublinmapper.util.StringProvider
import io.reactivex.Single
import io.rtpi.api.BusEireannLiveData
import io.rtpi.api.BusEireannStop
import io.rtpi.api.Service
import io.rtpi.client.RtpiClient
import ma.glasnost.orika.MapperFacade
import javax.inject.Named
import javax.inject.Singleton

@Module
class BusEireannRepositoryModule {

    @Provides
    @Singleton
    fun busEireannStopRepository(
        client: RtpiClient,
        localResource: BusEireannStopLocalResource,
        persisterDao: PersisterDao,
        stringProvider: StringProvider,
        internetManager: InternetManager,
        mapper: MapperFacade,
        @Named("LONG_TERM") memoryPolicy: MemoryPolicy
    ): Repository<DetailedBusEireannStop> {
        val fetcher = Fetcher<List<BusEireannStop>, Service> { Single.just(client.busEireann().getStops()) }
        val persister = BusEireannStopPersister(localResource, mapper, memoryPolicy, persisterDao, internetManager)
        val store = StoreRoom.from(fetcher, persister, StalePolicy.REFRESH_ON_STALE, memoryPolicy)
        return BusEireannStopRepository(store)
    }

    @Provides
    @Singleton
    fun luasRealTimeDataRepository(
        client: RtpiClient,
        @Named("SHORT_TERM") memoryPolicy: MemoryPolicy
    ): Repository<BusEireannLiveData> {
        val store = StoreBuilder.key<String, List<BusEireannLiveData>>()
//            .fetcher { stopId -> client.busEireann().getLiveData(stopId = stopId) }
            .fetcher { stopId -> Single.just(client.busEireann().getLiveData(stopId = stopId)) }
            .memoryPolicy(memoryPolicy)
            .open()
        return BusEireannLiveDataRepository(store)
    }

}

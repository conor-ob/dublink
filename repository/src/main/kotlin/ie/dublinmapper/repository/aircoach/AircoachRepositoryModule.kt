package ie.dublinmapper.repository.aircoach

import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import com.nytimes.android.external.store3.base.impl.StalePolicy
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import dagger.Module
import dagger.Provides
import ie.dublinmapper.datamodel.TxRunner
import ie.dublinmapper.datamodel.aircoach.AircoachStopDao
import ie.dublinmapper.datamodel.aircoach.AircoachStopLocalResource
import ie.dublinmapper.datamodel.aircoach.AircoachStopServiceDao
import ie.dublinmapper.datamodel.favourite.FavouriteDao
import ie.dublinmapper.datamodel.persister.PersisterDao
import ie.dublinmapper.domain.model.AircoachLiveData
import ie.dublinmapper.domain.model.AircoachStop
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.repository.aircoach.livedata.AircoachLiveDataRepository
import ie.dublinmapper.repository.aircoach.stops.AircoachStopPersister
import ie.dublinmapper.repository.aircoach.stops.AircoachStopRepository
import ie.dublinmapper.service.aircoach.AircoachStopRemoteResource
import ie.dublinmapper.service.aircoach.AircoachStopJson
import ie.dublinmapper.service.aircoach.ServiceResponseJson
import ie.dublinmapper.util.InternetManager
import ie.dublinmapper.util.Service
import ma.glasnost.orika.MapperFacade
import javax.inject.Named
import javax.inject.Singleton

@Module
class AircoachRepositoryModule {

    @Provides
    @Singleton
    fun aircoachStopRepository(
        remoteResource: AircoachStopRemoteResource,
        localResource: AircoachStopLocalResource,
        persisterDao: PersisterDao,
        internetManager: InternetManager,
        mapper: MapperFacade,
        @Named("LONG_TERM") memoryPolicy: MemoryPolicy
    ): Repository<AircoachStop> {
        val fetcher = Fetcher<List<AircoachStopJson>, Service> { remoteResource.getStops() }
        val persister = AircoachStopPersister(localResource, mapper, memoryPolicy, persisterDao, internetManager)
        val store = StoreRoom.from(fetcher, persister, StalePolicy.REFRESH_ON_STALE, memoryPolicy)
        return AircoachStopRepository(store)
    }

    @Provides
    @Singleton
    fun aircoachLiveDataRepository(
        remoteResource: AircoachStopRemoteResource,
        mapper: MapperFacade,
        @Named("SHORT_TERM") memoryPolicy: MemoryPolicy
    ): Repository<AircoachLiveData> {
        val store = StoreBuilder.parsedWithKey<String, ServiceResponseJson, List<AircoachLiveData>>()
            .fetcher { stopId -> remoteResource.getLiveData(stopId) }
            .parser { liveData -> mapper.mapAsList(liveData.services, AircoachLiveData::class.java).filter { it.dueTime.first().minutes >= 0 } } //TODO
            .memoryPolicy(memoryPolicy)
            .open()
        return AircoachLiveDataRepository(store)
    }

}

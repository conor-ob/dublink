package ie.dublinmapper.repository.aircoach

import com.nytimes.android.external.store3.base.impl.StoreBuilder
import dagger.Module
import dagger.Provides
import ie.dublinmapper.domain.model.AircoachLiveData
import ie.dublinmapper.domain.model.AircoachStop
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.service.aircoach.AircoachResource
import ie.dublinmapper.service.aircoach.AircoachStopJson
import ie.dublinmapper.service.aircoach.ServiceResponseJson
import javax.inject.Singleton

@Module
class AircoachRepositoryModule {

    @Provides
    @Singleton
    fun aircoachStopRepository(
        resource: AircoachResource
    ): Repository<AircoachStop> {
        val store = StoreBuilder.parsedWithKey<String, List<AircoachStopJson>, List<AircoachStop>>()
            .fetcher { resource.getStops() }
            .parser { stops -> AircoachStopMapper.map(stops) }
            .open()
        return AircoachStopRepository(store)
    }

    @Provides
    @Singleton
    fun aircoachLiveDataRepository(
        resource: AircoachResource
    ): Repository<AircoachLiveData> {
        val store = StoreBuilder.parsedWithKey<String, ServiceResponseJson, List<AircoachLiveData>>()
            .fetcher { stopId -> resource.getLiveData(stopId) }
            .parser { liveData -> AircoachLiveDataMapper.map(liveData.services) }
            .open()
        return AircoachLiveDataRepository(store)
    }

}

package ie.dublinmapper.repository.aircoach

import com.nytimes.android.external.store3.base.impl.StoreBuilder
import dagger.Module
import dagger.Provides
import ie.dublinmapper.domain.model.AircoachLiveData
import ie.dublinmapper.domain.model.AircoachStop
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.service.aircoach.AircoachApi
import ie.dublinmapper.service.aircoach.AircoachStopJson
import ie.dublinmapper.service.aircoach.ServiceResponseJson
import ie.dublinmapper.service.github.GithubApi
import ie.dublinmapper.util.Operator
import java.util.*
import javax.inject.Singleton

@Module
class AircoachRepositoryModule {

    @Provides
    @Singleton
    fun aircoachStopRepository(
        api: GithubApi
    ): Repository<AircoachStop> {
        val fetcher = AircoachStopFetcher(api)
        val store = StoreBuilder.parsedWithKey<String, List<AircoachStopJson>, List<AircoachStop>>()
            .fetcher(fetcher)
            .parser { stops -> AircoachStopMapper.map(stops) }
            .open()
        return AircoachStopRepository(store)
    }

    @Provides
    @Singleton
    fun airocachLiveDataRepository(
        api: AircoachApi
    ): Repository<AircoachLiveData> {
        val fetcher = AircoachLiveDataFetcher(api)
        val store = StoreBuilder.parsedWithKey<String, ServiceResponseJson, List<AircoachLiveData>>()
            .fetcher(fetcher)
            .parser { liveData -> Collections.singletonList(AircoachLiveData(dueTime = emptyList(),
                operator = Operator.SWORDS_EXPRESS,
                destination = "",
                direction = "")) }
            .open()
        return AircoachLiveDataRepository(store)
    }

}

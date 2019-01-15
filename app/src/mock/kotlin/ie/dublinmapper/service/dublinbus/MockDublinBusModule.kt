package ie.dublinmapper.service.dublinbus

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MockDublinBusModule {

    @Provides
    @Singleton
    fun dublinBusApi(): DublinBusApi  = MockDublinBusApi()

}

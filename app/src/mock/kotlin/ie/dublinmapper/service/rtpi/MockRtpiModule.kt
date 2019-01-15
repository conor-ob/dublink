package ie.dublinmapper.service.rtpi

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MockRtpiModule {

    @Provides
    @Singleton
    fun rtpiApi(): RtpiApi = MockRtpiApi()

}

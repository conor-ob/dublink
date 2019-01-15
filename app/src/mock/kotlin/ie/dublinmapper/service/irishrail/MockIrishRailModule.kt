package ie.dublinmapper.service.irishrail

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MockIrishRailModule {

    @Provides
    @Singleton
    fun irishRailApi(): IrishRailApi = MockIrishRailApi()

}

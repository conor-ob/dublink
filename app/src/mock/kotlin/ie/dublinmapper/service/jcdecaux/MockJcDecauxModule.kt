package ie.dublinmapper.service.jcdecaux

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MockJcDecauxModule {

    @Provides
    @Singleton
    fun jcDecauxApi(): JcDecauxApi = MockJcDecauxApi()

}

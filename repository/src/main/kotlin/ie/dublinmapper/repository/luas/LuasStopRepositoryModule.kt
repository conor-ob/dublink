package ie.dublinmapper.repository.luas

import dagger.Module
import dagger.Provides
import ie.dublinmapper.domain.model.luas.LuasStop
import ie.dublinmapper.domain.repository.Repository
import javax.inject.Singleton

@Module
class LuasStopRepositoryModule {

    @Provides
    @Singleton
    fun luasStopRepository(): Repository<LuasStop> {
        return LuasStopRepository()
    }

}

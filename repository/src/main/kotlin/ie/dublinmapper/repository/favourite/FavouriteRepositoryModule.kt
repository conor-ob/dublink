package ie.dublinmapper.repository.favourite

import dagger.Module
import dagger.Provides
import ie.dublinmapper.datamodel.favourite.FavouriteServiceLocationLocalResource
import ie.dublinmapper.domain.repository.FavouriteRepository
import ma.glasnost.orika.MapperFacade
import javax.inject.Singleton

@Module
class FavouriteRepositoryModule {

    @Provides
    @Singleton
    fun provideFavouriteRepository(
        localResource: FavouriteServiceLocationLocalResource,
        mapper: MapperFacade
    ): FavouriteRepository {
        return FavouriteServiceLocationRepository(localResource, mapper)
    }

}

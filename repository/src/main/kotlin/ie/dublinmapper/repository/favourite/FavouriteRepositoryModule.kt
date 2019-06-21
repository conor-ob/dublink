package ie.dublinmapper.repository.favourite

import dagger.Module
import dagger.Provides
import ie.dublinmapper.data.favourite.FavouriteDao
import ie.dublinmapper.data.favourite.FavouriteServiceLocationCacheResource
import ie.dublinmapper.domain.repository.FavouriteRepository
import ma.glasnost.orika.MapperFacade
import javax.inject.Singleton

@Module
class FavouriteRepositoryModule {

    @Provides
    @Singleton
    fun provideFavouriteRepository(
        cacheResource: FavouriteServiceLocationCacheResource,
        mapper: MapperFacade
    ): FavouriteRepository {
        return FavouriteServiceLocationRepository(cacheResource, mapper)
    }

}

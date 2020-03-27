package ie.dublinmapper.repository.favourite

import dagger.Module
import dagger.Provides
import ie.dublinmapper.datamodel.FavouriteServiceLocationLocalResource
import ie.dublinmapper.domain.repository.FavouriteRepository
import javax.inject.Singleton

@Module
class FavouriteRepositoryModule {

    @Provides
    @Singleton
    fun provideFavouriteRepository(
        localResource: FavouriteServiceLocationLocalResource
    ): FavouriteRepository {
        return FavouriteServiceLocationRepository(localResource)
    }
}

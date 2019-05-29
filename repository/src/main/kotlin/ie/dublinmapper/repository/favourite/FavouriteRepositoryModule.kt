package ie.dublinmapper.repository.favourite

import dagger.Module
import dagger.Provides
import ie.dublinmapper.data.favourite.FavouriteDao
import ie.dublinmapper.domain.repository.FavouriteRepository
import javax.inject.Singleton

@Module
class FavouriteRepositoryModule {

    @Provides
    @Singleton
    fun provideFavouriteRepository(dao: FavouriteDao): FavouriteRepository {
        return FavouriteServiceLocationRepository(dao)
    }

}

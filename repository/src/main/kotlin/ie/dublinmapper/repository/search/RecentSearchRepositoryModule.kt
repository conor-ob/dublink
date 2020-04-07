package ie.dublinmapper.repository.search

import dagger.Module
import dagger.Provides
import ie.dublinmapper.domain.datamodel.RecentServiceLocationSearchLocalResource
import ie.dublinmapper.domain.repository.RecentServiceLocationSearchRepository
import javax.inject.Singleton

@Module
class RecentSearchRepositoryModule {

    @Provides
    @Singleton
    fun provideRecentSearchRepository(
        recentServiceLocationSearchLocalResource: RecentServiceLocationSearchLocalResource
    ): RecentServiceLocationSearchRepository = DefaultRecentServiceLocationSearchRepository(
        recentServiceLocationSearchLocalResource
    )
}

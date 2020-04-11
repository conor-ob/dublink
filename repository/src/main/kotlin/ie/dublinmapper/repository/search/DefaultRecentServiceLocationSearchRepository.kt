package ie.dublinmapper.repository.search

import ie.dublinmapper.domain.datamodel.RecentServiceLocationSearchLocalResource
import ie.dublinmapper.domain.model.RecentServiceLocationSearch
import ie.dublinmapper.domain.repository.RecentServiceLocationSearchRepository
import io.reactivex.Observable

class DefaultRecentServiceLocationSearchRepository(
    private val recentServiceLocationSearchLocalResource: RecentServiceLocationSearchLocalResource
) : RecentServiceLocationSearchRepository {

    override fun getRecentSearches(): Observable<List<RecentServiceLocationSearch>> {
        return recentServiceLocationSearchLocalResource
            .selectRecentSearches()
            .map { searches -> searches.sortedByDescending { it.timestamp }.take(30) }
    }

    override fun saveRecentSearch(recentSearch: RecentServiceLocationSearch) {
        recentServiceLocationSearchLocalResource.insertRecentSearch(recentSearch)
    }
}

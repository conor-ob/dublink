package io.dublink.repository.search

import io.dublink.domain.datamodel.RecentServiceLocationSearchLocalResource
import io.dublink.domain.model.RecentServiceLocationSearch
import io.dublink.domain.repository.RecentServiceLocationSearchRepository
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

    override fun clearRecentSearches() {
        recentServiceLocationSearchLocalResource.deleteRecentSearches()
    }
}

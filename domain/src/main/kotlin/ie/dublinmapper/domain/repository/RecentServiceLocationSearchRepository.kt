package ie.dublinmapper.domain.repository

import ie.dublinmapper.domain.model.RecentServiceLocationSearch
import io.reactivex.Observable

interface RecentServiceLocationSearchRepository {

    fun getRecentSearches(): Observable<List<RecentServiceLocationSearch>>

    fun saveRecentSearch(recentSearch: RecentServiceLocationSearch)
}

package ie.dublinmapper.domain.datamodel

import ie.dublinmapper.domain.model.RecentServiceLocationSearch
import io.reactivex.Observable

interface RecentServiceLocationSearchLocalResource {

    fun selectRecentSearches(): Observable<List<RecentServiceLocationSearch>>

    fun insertRecentSearch(recentSearch: RecentServiceLocationSearch)

    fun deleteRecentSearches()
}

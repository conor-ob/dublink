package io.dublink.domain.datamodel

import io.dublink.domain.model.RecentServiceLocationSearch
import io.reactivex.Observable

interface RecentServiceLocationSearchLocalResource {

    fun selectRecentSearches(): Observable<List<RecentServiceLocationSearch>>

    fun insertRecentSearch(recentSearch: RecentServiceLocationSearch)

    fun deleteRecentSearches()
}

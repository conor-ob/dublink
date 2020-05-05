package io.dublink.domain.repository

import io.dublink.domain.model.RecentServiceLocationSearch
import io.reactivex.Observable

interface RecentServiceLocationSearchRepository {

    fun getRecentSearches(): Observable<List<RecentServiceLocationSearch>>

    fun saveRecentSearch(recentSearch: RecentServiceLocationSearch)

    fun clearRecentSearches()
}

package io.dublink.search

import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import io.rtpi.api.Service

sealed class Action : BaseAction {
    data class Search(val query: String) : Action()
    object GetNearbyLocations : Action()
    object GetRecentSearches : Action()
    object ClearRecentSearches : Action()
    data class AddRecentSearch(val service: Service, val locationId: String) : Action()
}

sealed class Change {
    object Loading : Change()
    data class Error(val throwable: Throwable) : Change()
    data class NearbyLocations(val nearbyLocations: NearbyLocationsResponse) : Change()
    data class SearchResults(val searchResults: SearchResultsResponse) : Change()
    data class RecentSearches(val recentSearches: RecentSearchesResponse) : Change()
    object AddRecentSearch : Change()
    object ClearRecentSearches : Change()
}

data class State(
    val loading: Boolean? = null,
    val scrollToTop: Boolean? = null,
    val throwable: Throwable? = null,
    val nearbyLocations: NearbyLocationsResponse? = null,
    val searchResults: SearchResultsResponse? = null,
    val recentSearches: RecentSearchesResponse? = null
) : BaseState

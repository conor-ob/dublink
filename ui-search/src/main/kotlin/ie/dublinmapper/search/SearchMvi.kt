package ie.dublinmapper.search

import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState

sealed class Action : BaseAction {
    data class Search(val query: String) : Action()
    object GetNearbyLocations : Action()
    object GetRecentSearches : Action()
}

sealed class Change {
    data class NearbyLocations(val nearbyLocations: NearbyLocationsResponse) : Change()
    data class SearchResults(val searchResults: SearchResultsResponse) : Change()
    data class RecentSearches(val recentSearches: RecentSearchesResponse) : Change()
}

data class State(
    val nearbyLocations: NearbyLocationsResponse? = null,
    val searchResults: SearchResultsResponse? = null,
    val recentSearches: RecentSearchesResponse? = null
) : BaseState

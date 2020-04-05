package ie.dublinmapper.search

import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.xwray.groupie.Group

sealed class Action : BaseAction {
    data class Search(val query: String) : Action()
    object GetNearbyLocations : Action()
    object GetRecentSearches : Action()
}

sealed class Change {
    object Loading : Change()
    data class NearbyLocations(val nearbyLocations: Group) : Change()
    data class SearchResults(val searchResults: Group) : Change()
    data class SearchResultsError(val throwable: Throwable?) : Change()
}

data class State(
    val isLoading: Boolean,
    val nearbyLocations: Group? = null,
    val searchResults: Group? = null,
    val isError: Boolean = false
) : BaseState

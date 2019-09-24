package ie.dublinmapper.search

import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.xwray.groupie.Group

sealed class Action : BaseAction {
    data class Search(val query: String) : Action()
}

sealed class Change {
    object Loading : Change()
    data class SearchResults(val searchResults: Group) : Change()
    data class SearchResultsError(val throwable: Throwable?) : Change()
}

data class State(
    val isLoading: Boolean,
    val searchResults: Group? = null,
    val isError: Boolean = false
) : BaseState

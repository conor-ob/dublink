package ie.dublinmapper.favourites

import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.xwray.groupie.Group

sealed class Action : BaseAction {
    object GetFavourites : Action()
}

sealed class Change {
    object Loading : Change()
    data class GetFavourites(val favourites: Group) : Change()
    data class GetFavouritesError(val throwable: Throwable?) : Change()
}

data class State(
    val isLoading: Boolean,
    val favourites: Group? = null,
    val isError: Boolean = false
) : BaseState

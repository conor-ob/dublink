package ie.dublinmapper.favourites

import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.xwray.groupie.Group
import ie.dublinmapper.domain.internet.InternetStatus

sealed class Action : BaseAction {
    object GetFavourites : Action()
    object SubscribeToInternetStatusChanges : Action()
}

sealed class Change {
    data class GetFavourites(val favourites: Group) : Change()
    data class GetFavouritesError(val throwable: Throwable?) : Change()
    data class InternetStatusChange(val internetStatusChange: InternetStatus) : Change()
}

data class State(
    val favourites: Group?,
    val internetStatusChange: InternetStatus?,
    val isError: Boolean
) : BaseState

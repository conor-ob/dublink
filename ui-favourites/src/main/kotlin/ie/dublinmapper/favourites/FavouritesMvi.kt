package ie.dublinmapper.favourites

import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import ie.dublinmapper.domain.internet.InternetStatus

sealed class Action : BaseAction {
    object GetFavouritesWithLiveData : Action()
    object SubscribeToInternetStatusChanges : Action()
}

sealed class Change {
    data class FavouritesWithLiveData(val favouritesWithLiveData: List<LiveDataPresentationResponse>) : Change()
    data class InternetStatusChange(val internetStatusChange: InternetStatus) : Change()
}

data class State(
    val favouritesWithLiveData: List<LiveDataPresentationResponse>?,
    val internetStatusChange: InternetStatus?
) : BaseState

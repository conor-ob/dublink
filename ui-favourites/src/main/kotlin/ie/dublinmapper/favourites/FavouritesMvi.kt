package ie.dublinmapper.favourites

import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import ie.dublinmapper.domain.internet.InternetStatus

sealed class Action : BaseAction {
    object GetFavourites : Action()
    object GetLiveData : Action()
    object SubscribeToInternetStatusChanges : Action()
}

sealed class Change {
    data class GetFavourites(val favourites: FavouritesPresentationResponse) : Change()
    data class GetLiveData(val liveData: List<LiveDataPresentationResponse>) : Change()
    data class InternetStatusChange(val internetStatusChange: InternetStatus) : Change()
}

data class State(
    val favourites: FavouritesPresentationResponse?,
    val liveData: List<LiveDataPresentationResponse>?,
    val internetStatusChange: InternetStatus?
) : BaseState

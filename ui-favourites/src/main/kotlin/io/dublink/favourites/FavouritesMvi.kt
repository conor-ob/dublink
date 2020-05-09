package io.dublink.favourites

import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import io.dublink.domain.internet.InternetStatus
import io.dublink.domain.model.DubLinkServiceLocation

sealed class Action : BaseAction {
    object GetFavourites : Action()
    object GetLiveData : Action()
    object RefreshLiveData : Action()
    object SubscribeToInternetStatusChanges : Action()
}

sealed class NewState {
    data class Favourites(val favourites: List<DubLinkServiceLocation>) : NewState()
    data class FavouritesWithLiveData(val favouritesWithLiveData: List<LiveDataPresentationResponse>) : NewState()
    data class InternetStatusChange(val internetStatusChange: InternetStatus) : NewState()
    object ClearLiveData : NewState()
    object RefreshIfStale : NewState()
}

data class State(
    val isLoading: Boolean,
    val favourites: List<DubLinkServiceLocation>?,
    val favouritesWithLiveData: List<LiveDataPresentationResponse>?,
    val internetStatusChange: InternetStatus?
) : BaseState

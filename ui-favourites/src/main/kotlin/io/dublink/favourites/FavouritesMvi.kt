package io.dublink.favourites

import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import io.dublink.domain.internet.InternetStatus

sealed class Action : BaseAction {
    object GetFavourites : Action()
    object GetLiveData : Action()
    object RefreshLiveData : Action()
    object SubscribeToInternetStatusChanges : Action()
}

sealed class NewState {
    data class Favourites(val favourites: List<ServiceLocationPresentationResponse>) : NewState()
    data class FavouritesWithLiveData(val favouritesWithLiveData: List<LiveDataPresentationResponse>) : NewState()
    data class InternetStatusChange(val internetStatusChange: InternetStatus) : NewState()
    object ClearLiveData : NewState()
    object RefreshIfStale : NewState()
}

data class State(
    val isLoading: Boolean,
    val favourites: List<ServiceLocationPresentationResponse>?,
    val favouritesWithLiveData: List<LiveDataPresentationResponse>?,
    val internetStatusChange: InternetStatus?
) : BaseState

package io.dublink.favourites

import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState

sealed class Action : BaseAction {
    object GetFavourites : Action()
    object GetLiveData : Action()
    object RefreshLiveData : Action()
}

sealed class NewState {
    data class Favourites(val favourites: List<ServiceLocationPresentationResponse>) : NewState()
    data class FavouritesWithLiveData(val favouritesWithLiveData: List<LiveDataPresentationResponse>) : NewState()
    object ClearLiveData : NewState()
    object RefreshIfStale : NewState()
}

data class State(
    val isLoading: Boolean,
    val favourites: List<ServiceLocationPresentationResponse>?,
    val favouritesWithLiveData: List<LiveDataPresentationResponse>?
) : BaseState

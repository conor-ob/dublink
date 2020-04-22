package ie.dublinmapper.favourites

import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import ie.dublinmapper.domain.internet.InternetStatus
import io.rtpi.api.ServiceLocation

sealed class Action : BaseAction {
    data class GetFavouritesWithLiveData(val showLoading: Boolean) : Action()
    data class FavouriteMovedToTop(val serviceLocation: ServiceLocation) : Action()
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

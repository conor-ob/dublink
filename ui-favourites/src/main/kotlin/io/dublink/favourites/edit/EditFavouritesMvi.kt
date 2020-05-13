package io.dublink.favourites.edit

import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.repository.ServiceLocationResponse

sealed class Action : BaseAction {
    object GetFavourites : Action()
    data class EditFavourite(val serviceLocation: DubLinkServiceLocation) : Action()
    data class FavouritesReordered(val serviceLocations: List<DubLinkServiceLocation>) : Action()
    data class SaveChanges(val serviceLocations: List<DubLinkServiceLocation>) : Action()
}

sealed class Result {
    data class FavouritesReceived(val favourites: List<DubLinkServiceLocation>, val errorResponses: List<ServiceLocationResponse.Error>) : Result()
    data class FavouriteEdited(val serviceLocation: DubLinkServiceLocation) : Result()
    data class FavouritesReordered(val serviceLocations: List<DubLinkServiceLocation>) : Result()
    data class Error(val throwable: Throwable) : Result()
    object FavouritesSaved : Result()
}

data class State(
    val original: List<DubLinkServiceLocation>? = null,
    val editing: List<DubLinkServiceLocation>? = null,
    val errorResponses: List<ServiceLocationResponse.Error>,
    val toastMessage: String? = null,
    val isFinished: Boolean
) : BaseState

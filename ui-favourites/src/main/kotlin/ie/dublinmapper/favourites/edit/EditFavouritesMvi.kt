package ie.dublinmapper.favourites.edit

import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import io.rtpi.api.ServiceLocation

sealed class Action : BaseAction {
    object GetFavourites : Action()
    data class EditFavourite(val serviceLocation: ServiceLocation) : Action()
    data class FavouritesReordered(val serviceLocations: List<ServiceLocation>) : Action()
    data class SaveChanges(val serviceLocations: List<ServiceLocation>) : Action()
}

sealed class Result {
    data class FavouritesReceived(val serviceLocations: List<ServiceLocation>) : Result()
    data class FavouriteEdited(val serviceLocation: ServiceLocation) : Result()
    data class FavouritesReordered(val serviceLocations: List<ServiceLocation>) : Result()
    object FavouritesSaved : Result()
}

data class State(
    val original: List<ServiceLocation>? = null,
    val editing: List<ServiceLocation>? = null,
    val isFinished: Boolean? = null
) : BaseState

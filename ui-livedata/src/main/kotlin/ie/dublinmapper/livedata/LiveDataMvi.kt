package ie.dublinmapper.livedata

import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import io.rtpi.api.Service

sealed class Action : BaseAction {

    data class GetServiceLocation(
        val serviceLocationId: String,
        val serviceLocationService: Service
    ) : Action()

    data class GetLiveData(
        val serviceLocationId: String,
        val serviceLocationName: String,
        val serviceLocationService: Service
    ) : Action()

    data class SaveFavourite(
        val serviceLocationId: String,
        val serviceLocationName: String,
        val serviceLocationService: Service
    ) : Action()

    data class RemoveFavourite(
        val serviceLocationId: String,
        val serviceLocationService: Service
    ) : Action()

    object ClearRouteFilters : Action()
    object CollapseRouteFilters : Action()
}

sealed class Change {
    data class GetServiceLocation(val serviceLocationResponse: ServiceLocationPresentationResponse) : Change()
    data class GetLiveData(val liveDataResponse: LiveDataPresentationResponse) : Change()
    object FavouriteSaved : Change()
    object FavouriteRemoved : Change()
    object RouteFiltersCleared : Change()
}

data class State(
    val isLoading: Boolean,
    val serviceLocationResponse: ServiceLocationPresentationResponse? = null,
    val liveDataResponse: LiveDataPresentationResponse? = null,
    val isFavourite: Boolean? = null
) : BaseState

package ie.dublinmapper.livedata

import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation
import java.util.SortedSet

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
        val serviceLocation: ServiceLocation
    ) : Action()

    data class RemoveFavourite(
        val serviceLocationId: String,
        val serviceLocationService: Service
    ) : Action()

    data class RouteFilterIntent(
        val type: RouteFilterChangeType
    ) : Action()

    data class RouteFilterSheetMoved(
        val state: Int
    ) : Action()
}

sealed class RouteFilterChangeType {
    data class Add(val route: String) : RouteFilterChangeType()
    data class Remove(val route: String) : RouteFilterChangeType()
    object Clear : RouteFilterChangeType()
    object NoChange : RouteFilterChangeType()
}

sealed class Change {
    data class GetServiceLocation(val serviceLocationResponse: ServiceLocationPresentationResponse) : Change()
    data class GetLiveData(val liveDataResponse: LiveDataPresentationResponse) : Change()

    object FavouriteSaved : Change()
    object FavouriteRemoved : Change()

    data class RouteFilterChange(
        val type: RouteFilterChangeType
    ) : Change()

    class RouteFilterSheetMoved(
        val state: Int?
    ) : Change()
}

data class State(
    val isLoading: Boolean,
    val serviceLocationResponse: ServiceLocationPresentationResponse? = null,
    val liveDataResponse: LiveDataPresentationResponse? = null,
    val filteredLiveDataResponse: LiveDataPresentationResponse? = null,
    val activeRouteFilters: Set<String> = emptySet(),
    val isFavourite: Boolean? = null,
    val routeDiscrepancyState: RouteDiscrepancyState? = null,
    val routeFilterState: Int? = null
) : BaseState

data class RouteDiscrepancyState(
    val knownRoutes: SortedSet<String>,
    val routes: SortedSet<String>,
    val loggedDiscrepancies: SortedSet<String>
)

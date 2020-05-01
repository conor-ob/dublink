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
    data class AddRoute(val route: String) : RouteFilterChangeType()
    data class AddDirection(val direction: String) : RouteFilterChangeType()
    data class RemoveRoute(val route: String) : RouteFilterChangeType()
    data class RemoveDirection(val direction: String) : RouteFilterChangeType()
    object Clear : RouteFilterChangeType()
    object NoChange : RouteFilterChangeType()
}

sealed class Change {
    data class GetServiceLocation(val serviceLocation: ServiceLocation) : Change()
    data class GetLiveData(val liveDataResponse: LiveDataPresentationResponse) : Change()

    data class Error(val throwable: Throwable) : Change()

    data class FavouriteSaved(val serviceLocation: ServiceLocation) : Change()
    data class FavouriteRemoved(val serviceLocation: ServiceLocation) : Change()

    data class RouteFilterChange(
        val type: RouteFilterChangeType
    ) : Change()

    class RouteFilterSheetMoved(
        val state: Int?
    ) : Change()
}

data class State(
    val isLoading: Boolean,
    val toastMessage: String?,
    val serviceLocation: ServiceLocation?,
    val liveDataResponse: LiveDataPresentationResponse?,
    val filteredLiveDataResponse: LiveDataPresentationResponse?,
    val activeRouteFilters: Set<String>,
    val activeDirectionFilters: Set<String>,
    val routeDiscrepancyState: RouteDiscrepancyState?,
    val routeFilterState: Int?
) : BaseState

data class RouteDiscrepancyState(
    val knownRoutes: SortedSet<String>,
    val routes: SortedSet<String>,
    val loggedDiscrepancies: SortedSet<String>
)

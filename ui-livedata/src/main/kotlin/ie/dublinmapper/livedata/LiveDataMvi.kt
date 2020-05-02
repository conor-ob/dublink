package ie.dublinmapper.livedata

import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import ie.dublinmapper.domain.model.DubLinkServiceLocation
import ie.dublinmapper.domain.model.Filter
import io.rtpi.api.Service
import java.util.SortedSet
import kotlin.random.Random

sealed class Action : BaseAction {

    data class GetServiceLocation(
        val serviceLocationId: String,
        val serviceLocationService: Service
    ) : Action()

    data class GetLiveData(
        val serviceLocationId: String,
        val serviceLocationService: Service
    ) : Action()

    data class SaveFavourite(
        val serviceLocation: DubLinkServiceLocation
    ) : Action()

    object AddOrRemoveFavourite : Action()

    data class RemoveFavourite(
        val serviceLocationId: String,
        val serviceLocationService: Service
    ) : Action()

    data class FilterIntent(
        val type: FilterChangeType
    ) : Action()

    data class RouteFilterSheetMoved(
        val state: Int
    ) : Action()
}

sealed class FilterChangeType {
    data class Add(val filter: Filter) : FilterChangeType()
    data class Remove(val filter: Filter) : FilterChangeType()
    object Clear : FilterChangeType()
}

sealed class Change {
    data class GetServiceLocation(val serviceLocation: DubLinkServiceLocation) : Change()
    data class GetLiveData(val liveDataResponse: LiveDataPresentationResponse) : Change()

    data class Error(val throwable: Throwable) : Change()

    data class FavouriteSaved(val serviceLocation: DubLinkServiceLocation) : Change()
    data class FavouriteRemoved(val serviceLocation: DubLinkServiceLocation) : Change()

    data class RouteFilterChange(
        val type: FilterChangeType
    ) : Change()

    class RouteFilterSheetMoved(
        val state: Int?
    ) : Change()

    object AddOrRemoveFavourite : Change()
}

data class State(
    val isLoading: Boolean,
    val toastMessage: String?,
    val serviceLocation: DubLinkServiceLocation?,
    val liveDataResponse: LiveDataPresentationResponse?,
    val routeFilterState: Int?,
    val favouriteDialog: FavouriteDialog?,
    val routeDiscrepancyState: RouteDiscrepancyState?
) : BaseState

sealed class FavouriteDialog {

    abstract val serviceLocation: DubLinkServiceLocation

    data class Add(override val serviceLocation: DubLinkServiceLocation, val random: Int = Random.nextInt()) : FavouriteDialog()
    data class Remove(override val serviceLocation: DubLinkServiceLocation, val random: Int = Random.nextInt()) : FavouriteDialog()
}

data class RouteDiscrepancyState(
    val knownRoutes: SortedSet<String>,
    val routes: SortedSet<String>,
    val loggedDiscrepancies: SortedSet<String>
)

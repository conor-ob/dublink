package ie.dublinmapper.domain.model

import io.rtpi.api.DockLocation
import io.rtpi.api.Operator
import io.rtpi.api.ServiceLocation
import io.rtpi.api.StopLocation
import io.rtpi.util.AlphaNumericComparator

interface DubLinkServiceLocation : ServiceLocation {
    val defaultName: String
    val isFavourite: Boolean
    val favouriteSortIndex: Int
}

abstract class AbstractDubLinkServiceLocation(
    serviceLocation: ServiceLocation,
    favouriteMetadata: FavouriteMetadata?
) : DubLinkServiceLocation {

    override val id = serviceLocation.id
    override val name = favouriteMetadata?.name ?: serviceLocation.name
    override val service = serviceLocation.service
    override val coordinate = serviceLocation.coordinate
    override val properties = serviceLocation.properties

    override val defaultName = serviceLocation.name
    override val isFavourite = favouriteMetadata != null
    override val favouriteSortIndex = favouriteMetadata?.sortIndex ?: -1
}

data class DubLinkStopLocation(
    val stopLocation: StopLocation, // TODO make private
    val favouriteMetadata: FavouriteMetadata? = null
) : AbstractDubLinkServiceLocation(stopLocation, favouriteMetadata) {

    private val routes = stopLocation.routeGroups.flatMap { routeGroup ->
        routeGroup.routes.map { route -> Route(operator = routeGroup.operator, id = route) }
    }
        .sortedWith(
            Comparator { r0, r1 ->
                AlphaNumericComparator.compare(r0.id, r1.id)
            }
        )
        .map { route ->
            Filter.RouteFilter(isActive = false, route = route)
        }

    private val directions = if (isDartStation()) {
        listOf("Northbound", "Southbound")
    } else {
        emptyList()
    }
        .map { direction ->
            Filter.DirectionFilter(isActive = false, direction = direction)
        }

    val filters = routes
        .plus(directions)
        .map { filter ->
            if (favouriteMetadata != null) {
                when (filter) {
                    is Filter.RouteFilter -> {
                        if (favouriteMetadata.routes.contains(filter.route)) {
                            return@map filter.copy(isActive = true)
                        }
                    }
                    is Filter.DirectionFilter -> {
                        if (favouriteMetadata.directions.contains(filter.direction)) {
                            return@map filter.copy(isActive = true)
                        }
                    }
                }
            }
            return@map filter
        }
}

data class DubLinkDockLocation(
    val dockLocation: DockLocation,
    val favouriteMetadata: FavouriteMetadata? = null
) : AbstractDubLinkServiceLocation(dockLocation, favouriteMetadata) {

    val availableBikes = dockLocation.availableBikes
    val availableDocks = dockLocation.availableDocks
    val totalDocks = dockLocation.totalDocks
}

sealed class Filter {

    abstract val isActive: Boolean

    data class RouteFilter(
        override val isActive: Boolean,
        val route: Route
    ) : Filter()

    data class DirectionFilter(
        override val isActive: Boolean,
        val direction: String
    ) : Filter()
}

data class Route(val operator: Operator, val id: String)

data class FavouriteMetadata(
    val name: String? = null,
    val routes: List<Route> = emptyList(),
    val directions: List<String> = emptyList(),
    val sortIndex: Int = -1
)

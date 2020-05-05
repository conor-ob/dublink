package ie.dublinmapper.domain.model

import io.rtpi.api.Operator
import java.util.Objects

fun DubLinkServiceLocation.id(): Long = Objects.hash(service, id).toLong()

fun DubLinkStopLocation.isDartStation(): Boolean = stopLocation.routeGroups.map { it.operator }.contains(Operator.DART)

fun DubLinkStopLocation.isLuasStop(): Boolean = stopLocation.routeGroups.map { it.operator }.contains(Operator.LUAS)

fun DubLinkServiceLocation.withFavouriteMetadata(favouriteMetadata: FavouriteMetadata): DubLinkServiceLocation {
    return when (this) {
        is DubLinkDockLocation -> copy(favouriteMetadata = favouriteMetadata)
        is DubLinkStopLocation -> copy(favouriteMetadata = favouriteMetadata)
        else -> throw IllegalStateException("Unknown type: $this")
    }
}

fun DubLinkServiceLocation.setCustomName(name: String): DubLinkServiceLocation {
    return when (this) {
        is DubLinkDockLocation -> withFavouriteMetadata(
            favouriteMetadata = favouriteMetadata?.copy(
                name = name
            ) ?: FavouriteMetadata(
                name = name
            )
        )
        is DubLinkStopLocation -> withFavouriteMetadata(
            favouriteMetadata = favouriteMetadata?.copy(
                name = name
            ) ?: FavouriteMetadata(
                name = name
            )
        )
        else -> throw IllegalStateException("Unknown type: $this")
    }
}

fun DubLinkStopLocation.addFilter(filter: Filter): DubLinkServiceLocation {
    return when (filter) {
        is Filter.RouteFilter -> {
            val adjustedRoutes = favouriteMetadata?.routes?.toMutableList() ?: mutableListOf()
            if (!adjustedRoutes.contains(filter.route)) {
                adjustedRoutes.add(filter.route)
            }
            withFavouriteMetadata(
                favouriteMetadata = favouriteMetadata?.copy(
                    routes = adjustedRoutes
                ) ?: FavouriteMetadata(
                    name = name,
                    routes = listOf(filter.route)
                )
            )
        }
        is Filter.DirectionFilter -> {
            val adjustedDirections = favouriteMetadata?.directions?.toMutableList() ?: mutableListOf()
            if (!adjustedDirections.contains(filter.direction)) {
                adjustedDirections.add(filter.direction)
            }
            withFavouriteMetadata(
                favouriteMetadata = favouriteMetadata?.copy(
                    directions = adjustedDirections
                ) ?: FavouriteMetadata(
                    name = name,
                    directions = listOf(filter.direction)
                )
            )
        }
    }
}

fun DubLinkStopLocation.removeFilter(filter: Filter): DubLinkServiceLocation {
    return when (filter) {
        is Filter.RouteFilter -> {
            val adjustedRoutes = favouriteMetadata?.routes?.toMutableList() ?: mutableListOf()
            adjustedRoutes.remove(filter.route)
            withFavouriteMetadata(
                favouriteMetadata = favouriteMetadata?.copy(
                    name = name,
                    routes = adjustedRoutes
                ) ?: FavouriteMetadata(
                    name = name
                )
            )
        }
        is Filter.DirectionFilter -> {
            val adjustedDirections = favouriteMetadata?.directions?.toMutableList() ?: mutableListOf()
            adjustedDirections.remove(filter.direction)
            withFavouriteMetadata(
                favouriteMetadata = favouriteMetadata?.copy(
                    name = name,
                    directions = adjustedDirections
                ) ?: FavouriteMetadata(
                    name = name
                )
            )
        }
    }
}

fun DubLinkStopLocation.clearFilters(): DubLinkServiceLocation {
    return withFavouriteMetadata(
        favouriteMetadata = favouriteMetadata?.copy(
            routes = emptyList(),
            directions = emptyList()
        ) ?: FavouriteMetadata(
            name = name
        )
    )
}

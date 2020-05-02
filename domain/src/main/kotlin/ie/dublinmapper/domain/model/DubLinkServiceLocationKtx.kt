package ie.dublinmapper.domain.model

import io.rtpi.api.Operator

fun DubLinkStopLocation.isDartStation(): Boolean = stopLocation.routeGroups.map { it.operator }.contains(Operator.DART)

fun DubLinkServiceLocation.withFavouriteMetadata(favouriteMetadata: FavouriteMetadata): DubLinkServiceLocation {
    return when (this) {
        is DubLinkDockLocation -> copy(favouriteMetadata = favouriteMetadata)
        is DubLinkStopLocation -> copy(favouriteMetadata = favouriteMetadata)
        else -> throw IllegalStateException("Unknown type: $this")
    }
}

fun DubLinkServiceLocation.setCustomName(name: String?): DubLinkServiceLocation {
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
        is Filter.RouteFilter -> withFavouriteMetadata(
            favouriteMetadata = favouriteMetadata?.copy(
                routes = favouriteMetadata.routes.plus(filter.route)
            ) ?: FavouriteMetadata(
                routes = listOf(filter.route)
            )
        )
        is Filter.DirectionFilter -> withFavouriteMetadata(
            favouriteMetadata = favouriteMetadata?.copy(
                directions = favouriteMetadata.directions.plus(filter.direction)
            ) ?: FavouriteMetadata(
                directions = listOf(filter.direction)
            )
        )
    }
}

fun DubLinkStopLocation.removeFilter(filter: Filter): DubLinkServiceLocation {
    return when (filter) {
        is Filter.RouteFilter -> withFavouriteMetadata(
            favouriteMetadata = favouriteMetadata?.copy(
                routes = favouriteMetadata.routes.minus(filter.route)
            ) ?: FavouriteMetadata()
        )
        is Filter.DirectionFilter -> withFavouriteMetadata(
            favouriteMetadata = favouriteMetadata?.copy(
                directions = favouriteMetadata.directions.minus(filter.direction)
            ) ?: FavouriteMetadata()
        )
    }
}

fun DubLinkStopLocation.clearFilters(): DubLinkServiceLocation {
    return withFavouriteMetadata(
        favouriteMetadata = favouriteMetadata?.copy(
            routes = emptyList(),
            directions = emptyList()
        ) ?: FavouriteMetadata()
    )
}

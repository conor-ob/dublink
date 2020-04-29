package ie.dublinmapper.domain.model

import io.rtpi.api.DockLocation
import io.rtpi.api.Operator
import io.rtpi.api.RouteGroup
import io.rtpi.api.ServiceLocation
import io.rtpi.api.StopLocation
import io.rtpi.util.AlphaNumericComparator

private const val customPropertiesKey = "custom_properties"

private fun ServiceLocation.getCustomProperties(): CustomProperties {
    val customProperties = properties[customPropertiesKey]
    return if (customProperties == null) {
        CustomProperties(
            favouriteMetadata = FavouriteMetadata(
                isFavourite = false,
                customName = null,
                customRouteGroups = emptyList(),
                customDirections = emptyList(),
                customSortIndex = -1
            )
        )
    } else {
        customProperties as CustomProperties
    }
}

private fun ServiceLocation.setCustomProperties(newCustomProperties: CustomProperties): ServiceLocation {
    return when (this) {
        is StopLocation -> copy(properties = mapOf(customPropertiesKey to newCustomProperties))
        is DockLocation -> copy(properties = mapOf(customPropertiesKey to newCustomProperties))
        else -> TODO()
    }
}

fun ServiceLocation.setFavourite(): ServiceLocation {
    val customProperties = getCustomProperties()
    return setCustomProperties(
        newCustomProperties = customProperties.copy(
            favouriteMetadata = customProperties.favouriteMetadata.copy(isFavourite = true)
        )
    )
}

fun ServiceLocation.isFavourite(): Boolean = getCustomProperties().favouriteMetadata.isFavourite

fun ServiceLocation.setCustomName(name: String?): ServiceLocation {
    val customProperties = getCustomProperties()
    return setCustomProperties(
        newCustomProperties = customProperties.copy(
            favouriteMetadata = customProperties.favouriteMetadata.copy(customName = name)
        )
    )
}

fun ServiceLocation.getCustomName(): String? = getCustomProperties().favouriteMetadata.customName

fun ServiceLocation.getName(): String = getCustomName() ?: name

fun ServiceLocation.setSortIndex(sortIndex: Long): ServiceLocation {
    val customProperties = getCustomProperties()
    return setCustomProperties(
        newCustomProperties = customProperties.copy(
            favouriteMetadata = customProperties.favouriteMetadata.copy(customSortIndex = sortIndex.toInt())
        )
    )
}

// TODO change to Int
fun ServiceLocation.getSortIndex(): Long = getCustomProperties().favouriteMetadata.customSortIndex.toLong()

fun StopLocation.getSortedRoutes(): List<Pair<Operator, String>> =
    routeGroups.flatMap { routeGroup ->
        routeGroup.routes.map { route ->
            routeGroup.operator to route
        }
    }.sortedWith(
        Comparator { o1, o2 ->
            AlphaNumericComparator.compare(o1.second, o2.second)
        }
    )

fun ServiceLocation.getCustomRoutes(): List<RouteGroup> = getCustomProperties().favouriteMetadata.customRouteGroups

fun ServiceLocation.getCustomDirections(): List<String> = getCustomProperties().favouriteMetadata.customDirections

fun ServiceLocation.addCustomRoute(operator: Operator, route: String): ServiceLocation {
    val customProperties = getCustomProperties()
    val metadata = customProperties.favouriteMetadata
    val operatorsToRoutes = metadata.customRouteGroups.associate { it.operator to it.routes }.toSortedMap()
    val routes = operatorsToRoutes[operator]
    val adjustedRoutes = if (routes == null) {
        listOf(route)
    } else {
        routes.toMutableList().plus(route).toSet().sortedWith(AlphaNumericComparator)
    }
    operatorsToRoutes[operator] = adjustedRoutes
    return setCustomProperties(
        newCustomProperties = customProperties.copy(
            favouriteMetadata = customProperties.favouriteMetadata.copy(
                customRouteGroups = operatorsToRoutes.map {
                    RouteGroup(
                        operator = it.key,
                        routes = it.value
                    )
                }
            )
        )
    )
}

fun ServiceLocation.removeCustomRoute(operator: Operator, route: String): ServiceLocation {
    val customProperties = getCustomProperties()
    val metadata = customProperties.favouriteMetadata
    val operatorsToRoutes = metadata.customRouteGroups.associate { it.operator to it.routes }.toSortedMap()
    val routes = operatorsToRoutes[operator]
    val adjustedRoutes = if (routes == null) {
        // TODO shouldn't happen
        emptyList()
    } else {
        val copy = routes.toMutableList()
        copy.remove(route)
        copy.toSet().toList().sortedWith(AlphaNumericComparator)
    }
    if (adjustedRoutes.isNullOrEmpty()) {
        operatorsToRoutes.remove(operator)
    } else {
        operatorsToRoutes[operator] = adjustedRoutes
    }
    return setCustomProperties(
        newCustomProperties = customProperties.copy(
            favouriteMetadata = customProperties.favouriteMetadata.copy(
                customRouteGroups = operatorsToRoutes.map {
                    RouteGroup(
                        operator = it.key,
                        routes = it.value
                    )
                }
            )
        )
    )
}

fun ServiceLocation.addCustomDirection(direction: String): ServiceLocation {
    val customProperties = getCustomProperties()
    return setCustomProperties(
        newCustomProperties = customProperties.copy(
            favouriteMetadata = customProperties.favouriteMetadata.copy(
                customDirections = customProperties.favouriteMetadata.customDirections.plus(direction).toSet().toList()
            )
        )
    )
}

fun ServiceLocation.removeCustomDirection(direction: String): ServiceLocation {
    val customProperties = getCustomProperties()
    val copy = customProperties.favouriteMetadata.customDirections.toMutableList()
    copy.remove(direction)
    return setCustomProperties(
        newCustomProperties = customProperties.copy(
            favouriteMetadata = customProperties.favouriteMetadata.copy(
                customDirections = copy
            )
        )
    )
}

fun ServiceLocation.hasCustomRoute(operator: Operator, route: String): Boolean {
    val routeGroup = getCustomProperties().favouriteMetadata.customRouteGroups.find { it.operator == operator }
    return routeGroup?.routes?.contains(route) ?: false
}

fun ServiceLocation.hasCustomDirection(direction: String): Boolean {
    return getCustomProperties().favouriteMetadata.customDirections.contains(direction)
}

data class CustomProperties(
    val favouriteMetadata: FavouriteMetadata
)

data class FavouriteMetadata(
    val isFavourite: Boolean,
    val customName: String?,
    val customRouteGroups: List<RouteGroup>,
    val customDirections: List<String>,
    val customSortIndex: Int
)

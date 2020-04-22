package ie.dublinmapper.domain.model

import io.rtpi.api.Operator
import io.rtpi.api.ServiceLocation
import io.rtpi.api.StopLocation
import io.rtpi.util.AlphaNumericComparator

private const val favourite = "key_favourite"
private const val customName = "key_custom_name"
private const val customSortIndex = "key_custom_sort_index"

fun ServiceLocation.isFavourite(): Boolean = properties[favourite] as? Boolean? ?: false

fun ServiceLocation.setFavourite() {
    properties[favourite] = true
}

fun ServiceLocation.setCustomName(name: String) {
    properties[customName] = name
}

fun ServiceLocation.getCustomName(): String? = properties[customName] as? String?

fun ServiceLocation.getName(): String = properties[customName] as? String? ?: name

fun ServiceLocation.setSortIndex(sortIndex: Long) {
    properties[customSortIndex] = sortIndex
}

fun ServiceLocation.getSortIndex(): Long = properties[customSortIndex] as? Long? ?: -1L

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

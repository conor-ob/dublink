package ie.dublinmapper.domain.model

import io.rtpi.api.*

fun createLuasStop(
    id: String = "LUAS1",
    name: String = "St. Stephen's Green",
    coordinate: Coordinate = Coordinate(53.533, -6.232),
    routeGroups: List<RouteGroup> = emptyList()
): ServiceLocation = StopLocation(
    id = id,
    name = name,
    service = Service.LUAS,
    coordinate = coordinate,
    routeGroups = routeGroups
)

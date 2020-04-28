package ie.dublinmapper.domain.model

import io.rtpi.api.Coordinate
import io.rtpi.api.RouteGroup
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation
import io.rtpi.api.StopLocation

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

fun createDublinBusStop(
    id: String,
    name: String,
    coordinate: Coordinate,
    routeGroups: List<RouteGroup>
) = StopLocation(
    id = id,
    name = name,
    service = Service.DUBLIN_BUS,
    coordinate = coordinate,
    routeGroups = routeGroups
)

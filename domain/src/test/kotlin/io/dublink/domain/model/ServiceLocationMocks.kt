package io.dublink.domain.model

import io.rtpi.api.Coordinate
import io.rtpi.api.RouteGroup
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation
import io.rtpi.api.StopLocation

fun createIrishRailStation(
    id: String = "PERSE",
    name: String = "Pearse",
    coordinate: Coordinate = Coordinate(53.563, -6.252),
    routeGroups: List<RouteGroup> = emptyList()
) = StopLocation(
    id = id,
    name = name,
    service = Service.IRISH_RAIL,
    coordinate = coordinate,
    routeGroups = routeGroups
)

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

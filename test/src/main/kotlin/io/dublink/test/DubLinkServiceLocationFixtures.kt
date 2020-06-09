package io.dublink.test

import io.dublink.domain.model.DubLinkDockLocation
import io.dublink.domain.model.DubLinkStopLocation
import io.rtpi.api.Coordinate
import io.rtpi.api.RouteGroup
import io.rtpi.api.Service
import io.rtpi.test.fixtures.createDockLocation
import io.rtpi.test.fixtures.createStopLocation
import io.rtpi.test.fixtures.randomCoordinatePoint
import io.rtpi.test.fixtures.randomInt
import java.util.UUID

fun createDubLinkStopLocation(
    id: String = UUID.randomUUID().toString(),
    name: String = UUID.randomUUID().toString(),
    service: Service = Service.values().random(),
    coordinate: Coordinate = Coordinate(latitude = randomCoordinatePoint(), longitude = randomCoordinatePoint()),
    routeGroups: List<RouteGroup> = service.operators.map { operator ->
        RouteGroup(
            operator = operator,
            routes = listOf(UUID.randomUUID().toString())
        )
    }
) = DubLinkStopLocation(
    stopLocation = createStopLocation(id, name, service, coordinate, routeGroups)
)

fun createDubLinkDockLocation(
    id: String = UUID.randomUUID().toString(),
    name: String = UUID.randomUUID().toString(),
    service: Service = Service.values().random(),
    coordinate: Coordinate = Coordinate(latitude = randomCoordinatePoint(), longitude = randomCoordinatePoint()),
    availableBikes: Int = randomInt(),
    availableDocks: Int = randomInt(),
    totalDocks: Int = randomInt()
) = DubLinkDockLocation(
    dockLocation = createDockLocation(id, name, service, coordinate, availableBikes, availableDocks, totalDocks)
)

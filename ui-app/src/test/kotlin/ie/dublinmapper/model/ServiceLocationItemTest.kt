package ie.dublinmapper.model

import io.rtpi.api.Coordinate
import io.rtpi.api.Service
import io.rtpi.api.StopLocation

class ServiceLocationItemTest : AbstractUniqueItemTest<ServiceLocationItem>() {

    override val controlItem: ServiceLocationItem
        get() = ServiceLocationItem(
            serviceLocation = StopLocation(
                id = "3421",
                name = "Trinity College",
                service = Service.LUAS,
                coordinate = Coordinate(53.23092, -6.3423),
                routeGroups = emptyList()
            ),
            walkDistance = 386.12,
            icon = 0,
            routeGroups = null
        )

    override val sameItemNotEqual: ServiceLocationItem
        get() = ServiceLocationItem(
            serviceLocation = StopLocation(
                id = "3421",
                name = "Trinity College",
                service = Service.LUAS,
                coordinate = Coordinate(53.23092, -6.3423),
                routeGroups = emptyList()
            ),
            walkDistance = 193.776,
            icon = 0,
            routeGroups = null
        )

    override val equalItem: ServiceLocationItem
        get() = ServiceLocationItem(
            serviceLocation = StopLocation(
                id = "3421",
                name = "Trinity College",
                service = Service.LUAS,
                coordinate = Coordinate(53.23092, -6.3423),
                routeGroups = emptyList()
            ),
            walkDistance = 386.12,
            icon = 0,
            routeGroups = null
        )
}

package io.dublink.model

import io.dublink.domain.model.DubLinkStopLocation
import io.dublink.test.AbstractUniqueItemTest
import io.rtpi.api.Coordinate
import io.rtpi.api.Service
import io.rtpi.api.StopLocation
import org.junit.Test

class StopLocationItemTest : AbstractUniqueItemTest<StopLocationItem>() {

    override val controlItem: StopLocationItem
        get() = StopLocationItem(
            serviceLocation = DubLinkStopLocation(
                StopLocation(
                    id = "3421",
                    name = "Trinity College",
                    service = Service.LUAS,
                    coordinate = Coordinate(53.23092, -6.3423),
                    routeGroups = emptyList()
                )
            ),
            walkDistance = 386.12
        )

    override val sameItemNotEqual: StopLocationItem
        get() = StopLocationItem(
            serviceLocation = DubLinkStopLocation(
                StopLocation(
                    id = "3421",
                    name = "Trinity College",
                    service = Service.LUAS,
                    coordinate = Coordinate(53.23092, -6.3423),
                    routeGroups = emptyList()
                )
            ),
            walkDistance = 193.776
        )

    override val equalItem: StopLocationItem
        get() = StopLocationItem(
            serviceLocation = DubLinkStopLocation(
                StopLocation(
                    id = "3421",
                    name = "Trinity College",
                    service = Service.LUAS,
                    coordinate = Coordinate(53.23092, -6.3423),
                    routeGroups = emptyList()
                )
            ),
            walkDistance = 386.12
        )

    @Test
    override fun ignored() { }
}

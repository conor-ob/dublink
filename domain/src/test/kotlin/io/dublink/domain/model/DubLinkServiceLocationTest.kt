package io.dublink.domain.model

import com.google.common.truth.Truth.assertThat
import io.rtpi.api.Operator
import io.rtpi.api.RouteGroup
import org.junit.Test

class DubLinkServiceLocationTest {

    @Test
    fun `test filters`() {
        // arrange
        val dubLinkDartStation = DubLinkStopLocation(
            stopLocation = createIrishRailStation(
                id = "PERSE",
                name = "Dublin Pearse",
                routeGroups = listOf(
                    RouteGroup(
                        operator = Operator.COMMUTER,
                        routes = listOf(Operator.COMMUTER.fullName)
                    ),
                    RouteGroup(
                        operator = Operator.DART,
                        routes = listOf(Operator.DART.fullName)
                    ),
                    RouteGroup(
                        operator = Operator.INTERCITY,
                        routes = listOf(Operator.INTERCITY.fullName)
                    )
                )
            ),
            favouriteMetadata = FavouriteMetadata(
                isFavourite = true,
                name = "",
                routes = listOf(Route(operator = Operator.DART, id = Operator.DART.fullName)),
                directions = listOf("Southbound"),
                sortIndex = -1
            )
        )

        // act
        val filters = dubLinkDartStation.filters

        // assert
        assertThat(filters).containsExactly(
            Filter.RouteFilter(
                isActive = false,
                route = Route(operator = Operator.COMMUTER, id = Operator.COMMUTER.fullName)
            ),
            Filter.RouteFilter(
                isActive = true,
                route = Route(operator = Operator.DART, id = Operator.DART.fullName)
            ),
            Filter.RouteFilter(
                isActive = false,
                route = Route(operator = Operator.INTERCITY, id = Operator.INTERCITY.fullName)
            ),
            Filter.DirectionFilter(
                isActive = false,
                direction = "Northbound"
            ),
            Filter.DirectionFilter(
                isActive = true,
                direction = "Southbound"
            )
        )
    }
}

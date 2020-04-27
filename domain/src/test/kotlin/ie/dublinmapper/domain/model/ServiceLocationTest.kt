package ie.dublinmapper.domain.model

import com.google.common.truth.Truth.assertThat
import io.rtpi.api.Coordinate
import io.rtpi.api.Operator
import io.rtpi.api.RouteGroup
import org.junit.Test

class ServiceLocationTest {

    @Test
    fun `test the thing`() {
        val luasStop = createLuasStop()
        assertThat(luasStop.isFavourite()).isFalse()
        assertThat(luasStop.getName()).isEqualTo("St. Stephen's Green")

        luasStop.setFavourite()
        assertThat(luasStop.isFavourite()).isTrue()
        assertThat(luasStop.getName()).isEqualTo("St. Stephen's Green")

        luasStop.setCustomName("My Favourite Luas Stop")
        assertThat(luasStop.isFavourite()).isTrue()
        assertThat(luasStop.getName()).isEqualTo("My Favourite Luas Stop")
    }

    @Test
    fun `test properties`() {
        val dublinBusStop1 = createDublinBusStop(
            id = "444",
            name = "Kilmacud Road",
            coordinate = Coordinate(53.463, -6.22123),
            routeGroups = listOf(
                RouteGroup(
                    operator = Operator.DUBLIN_BUS,
                    routes = listOf("11", "47", "116")
                ),
                RouteGroup(
                    operator = Operator.GO_AHEAD,
                    routes = listOf("75", "75A")
                )
            )
        )

        val dublinBusStop2 = createDublinBusStop(
            id = "444",
            name = "Kilmacud Road",
            coordinate = Coordinate(53.463, -6.22123),
            routeGroups = listOf(
                RouteGroup(
                    operator = Operator.DUBLIN_BUS,
                    routes = listOf("11", "47", "116")
                ),
                RouteGroup(
                    operator = Operator.GO_AHEAD,
                    routes = listOf("75", "75A")
                )
            )
        )

        assertThat(dublinBusStop1).isEqualTo(dublinBusStop2)
        assertThat(dublinBusStop1.hashCode()).isEqualTo(dublinBusStop2.hashCode())

        dublinBusStop1.setSortIndex(3L)
        dublinBusStop2.setSortIndex(7L)

        assertThat(dublinBusStop1).isEqualTo(dublinBusStop2)
        assertThat(dublinBusStop1.hashCode()).isEqualTo(dublinBusStop2.hashCode())
    }

    @Test
    fun `test custom routes`() {
        val dublinBusStop = createDublinBusStop(
            id = "444",
            name = "Kilmacud Road",
            coordinate = Coordinate(53.463, -6.22123),
            routeGroups = listOf(
                RouteGroup(
                    operator = Operator.DUBLIN_BUS,
                    routes = listOf("11", "47", "116")
                ),
                RouteGroup(
                    operator = Operator.GO_AHEAD,
                    routes = listOf("75", "75A")
                )
            )
        )

        dublinBusStop.addCustomRoute(Operator.GO_AHEAD, "75")
        dublinBusStop.addCustomRoute(Operator.DUBLIN_BUS, "47")
    }

    @Test
    fun `test immutability`() {
        val dublinBusStop = createDublinBusStop(
            id = "444",
            name = "Kilmacud Road",
            coordinate = Coordinate(53.463, -6.22123),
            routeGroups = listOf(
                RouteGroup(
                    operator = Operator.DUBLIN_BUS,
                    routes = listOf("11", "47", "116")
                ),
                RouteGroup(
                    operator = Operator.GO_AHEAD,
                    routes = listOf("75", "75A")
                )
            )
        )

        val favourite = dublinBusStop.setFavourite()

        assertThat(dublinBusStop).isNotEqualTo(favourite)
    }
}

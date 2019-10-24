package ie.dublinmapper.domain.model

import com.google.common.truth.Truth.assertThat
import io.rtpi.api.Coordinate
import io.rtpi.api.LuasStop
import io.rtpi.api.Operator
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

}

private fun createLuasStop(
    id: String = "LUAS1",
    name: String = "St. Stephen's Green",
    coordinate: Coordinate = Coordinate(53.533, -6.232),
    operators: Set<Operator> = setOf(Operator.LUAS),
    routes: Map<Operator, List<String>> = mapOf(Operator.LUAS to listOf("Green"))
): LuasStop {
    return LuasStop(
        id = id,
        name = name,
        coordinate = coordinate,
        operators = operators,
        routes = routes
    )
}

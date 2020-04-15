package ie.dublinmapper.search

import com.google.common.truth.Truth.assertThat
import io.rtpi.api.Coordinate
import io.rtpi.api.Operator
import io.rtpi.api.RouteGroup
import io.rtpi.api.Service
import io.rtpi.api.StopLocation
import me.xdrop.fuzzywuzzy.FuzzySearch
import org.junit.Test

class SearchServiceTest {

    @Test
    fun `test fuzzy`() {
        val serviceLocations = listOf(
            StopLocation(
                id = "Busaras",
                name = "Busaras",
                service = Service.DUBLIN_BUS,
                coordinate = Coordinate(0.0, 0.0),
                routeGroups = emptyList()
            ),
            StopLocation(
                id = "123",
                name = "Busáras",
                service = Service.DUBLIN_BUS,
                coordinate = Coordinate(0.0, 0.0),
                routeGroups = listOf(
                    RouteGroup(
                        operator = Operator.DUBLIN_BUS,
                        routes = listOf("382")
                    ),
                    RouteGroup(
                        operator = Operator.GO_AHEAD,
                        routes = listOf("421")
                    ),
                    RouteGroup(
                        operator = Operator.GO_AHEAD,
                        routes = listOf("156")
                    )
                )
            )
        )

        val extractTop = FuzzySearch.extractSorted(
            "Busáras",
            serviceLocations
        ) {
//            "${it.id} ${it.name} ${it.service.fullName} ${it.routeGroups}"
            "${it.id}${it.name}${it.service.fullName}${it.routeGroups.joinToString(separator = "") { routeGroup -> routeGroup.operator.fullName }}".trim()
        }
        assertThat(extractTop).isNotNull()
    }
}

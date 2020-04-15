package ie.dublinmapper.search

import com.google.common.truth.Truth.assertThat
import io.rtpi.api.Coordinate
import io.rtpi.api.Operator
import io.rtpi.api.RouteGroup
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation
import io.rtpi.api.StopLocation
import me.xdrop.fuzzywuzzy.FuzzySearch
import me.xdrop.fuzzywuzzy.ToStringFunction
import me.xdrop.fuzzywuzzy.algorithms.RatioAlgorithm
import me.xdrop.fuzzywuzzy.algorithms.TokenSet
import me.xdrop.fuzzywuzzy.algorithms.TokenSort
import me.xdrop.fuzzywuzzy.algorithms.WeightedRatio
import org.junit.Test

class SearchServiceTest {

    private val searchService = SearchService()

    @Test
    fun `test fuzzy search`() {
        val serviceLocations = listOf(
            StopLocation(
                id = "825000039",
                name = "Booterstown (Rock Rd): opp Dart Station",
                service = Service.AIRCOACH,
                coordinate = Coordinate(0.0, 0.0),
                routeGroups = listOf(
                    RouteGroup(
                        operator = Operator.AIRCOACH,
                        routes = listOf("702", "703")
                    )
                )
            ),
            StopLocation(
                id = "PERSE",
                name = "Dublin Pearse",
                service = Service.IRISH_RAIL,
                coordinate = Coordinate(0.0, 0.0),
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
            )
        )

        val extractTop = searchService.search(
            "pearse dart",
            serviceLocations
        ).blockingFirst()

        val extractSorted = FuzzySearch.extractSorted(
            "pearse)   dart ./",
            serviceLocations,
            ToStringFunction<ServiceLocation> { it ->
                if (it is StopLocation) {
                    listOf(
                        it.name,
                        it.service.fullName
                    ).plus(
                        it.routeGroups.map { routeGroup -> routeGroup.operator.fullName }
                    )
                } else {
                    listOf(
                        it.name,
                        it.service.fullName
                    )
                }
                    .toSet()
                    .joinToString(separator = " ")
                //                    .joinToString(separator = " ") { value -> DefaultStringFunction.subNonAlphaNumeric(value.toLowerCase(), " ") }
            },
            TokenSet()
        )

        assertThat(extractTop).hasSize(2)
    }

    @Test
    fun `test algos`() {
        val query = "dart"
        val string1 = "Booterstown (Rock Rd): opp Dart Station Aircoach"
        val string2 = "Dublin Pearse Irish Rail Commuter DART InterCity"

//        val score1 = WeightedRatio().apply(query, string1)
        val score1 = TokenSet().apply(query, string1)
//        val score2 = WeightedRatio().apply(query, string2)
        val score2 = TokenSet().apply(query, string2)

        assertThat(score2).isGreaterThan(score1)
    }
}

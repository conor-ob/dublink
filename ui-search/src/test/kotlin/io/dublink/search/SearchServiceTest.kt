package io.dublink.search

import com.google.common.truth.Truth.assertThat
import io.dublink.domain.model.DubLinkDockLocation
import io.dublink.domain.model.DubLinkStopLocation
import io.reactivex.Single
import io.rtpi.api.DockLocation
import io.rtpi.api.Operator
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation
import io.rtpi.api.StopLocation
import io.rtpi.test.client.RtpiStaticDataClient
import org.junit.Test

class SearchServiceTest {

    /**
     * Tests to add
     * RDS
     * RTE
     * stephens green
     */

    companion object {

        private val searchService = SearchService()
    }

    private val rtpiStaticDataClient = RtpiStaticDataClient()
    private val services = listOf(
        Service.LUAS,
        Service.IRISH_RAIL,
        Service.DUBLIN_BIKES,
        Service.DUBLIN_BUS,
        Service.AIRCOACH,
        Service.BUS_EIREANN
    )
    private val serviceLocations = Single.zip(
        services.map { service ->
            rtpiStaticDataClient.getServiceLocations(service)
        }
    ) { serviceLocationStreams -> serviceLocationStreams
        .flatMap { it as List<ServiceLocation> }
        .map {
            when (it) {
                is DockLocation -> DubLinkDockLocation(it)
                is StopLocation -> DubLinkStopLocation(it)
                else -> throw IllegalStateException()
            }
        }
    }.blockingGet()

//    @Test
//    fun `searching a short term should produce an accurate result if the searchable data is small`() {
//        // arrange
//        val searchQuery = "da"
//        val luasStops = serviceLocations.filter { it.service == Service.LUAS }
//
//        // act
//        val searchResults = searchService.search(
//            query = searchQuery,
//            serviceLocations = luasStops
//        ).blockingFirst()
//
//        // assert
//        assertThat(searchResults).hasSize(1)
//        assertThat(searchResults.first().name).isEqualTo("Dawson")
//    }

//    @Test
//    fun `searching for a location and a service should produce an accurate result`() {
//        // arrange
//        val searchQuery = "pearse dart"
//
//        // act
//        val searchResults = searchService.search(
//            query = searchQuery,
//            serviceLocations = serviceLocations
//        ).blockingFirst()
//
//        // assert
//        assertThat(searchResults).isNotEmpty()
//        assertThat(searchResults.first().name).isEqualTo("Dublin Pearse")
//    }

    @Test
    fun `searching with slightly misspelled queries should produce accurate results`() {
        // arrange
        val searchQuery = "vlacktock"

        // act
        val searchResults = searchService.search(
            query = searchQuery,
            serviceLocations = serviceLocations
        ).blockingFirst()

        // assert
        assertThat(searchResults).isNotEmpty()
        assertThat(searchResults.take(5).map { it.name }).containsExactly(
            "Blackrock",
            "Blackrock",
            "Blackrock",
            "Blackrock Village",
            "Blackrock Road (Opp Kingswood)"
        ).inOrder()
    }

    @Test
    fun `searching for an operator should produce locations services by that operator`() {
        // arrange
        val searchQuery = "go ahead"

        // act
        val searchResults = searchService.search(
            query = searchQuery,
            serviceLocations = serviceLocations
        ).blockingFirst()

        // assert
        assertThat(searchResults).isNotEmpty()
        assertThat(
            searchResults.filterIsInstance<StopLocation>().all { stopLocation ->
                stopLocation.routeGroups.map { routeGroup ->
                    routeGroup.operator
                }.contains(Operator.GO_AHEAD)
            }
        ).isTrue()
    }

//    @Test
//    fun `searching for random letters should not produce any results`() {
//        // arrange
//        val searchQuery = "pqtw"
//
//        // act
//        val searchResults = searchService.search(
//            query = searchQuery,
//            serviceLocations = serviceLocations
//        ).blockingFirst()
//
//        // assert
//        assertThat(searchResults).isEmpty()
//    }

    @Test
    fun `searching for a general term should produce relevant results`() {
        // arrange
        val searchQuery = "dock"

        // act
        val searchResults = searchService.search(
            query = searchQuery,
            serviceLocations = serviceLocations
        ).blockingFirst()

        // assert
        assertThat(searchResults.take(5).map { it.name }).containsExactly(
            "George's Dock",
            "Spencer Dock",
            "Kilcock",
            "Castleknock",
            "Blackrock"
        ).inOrder()
    }

    @Test
    fun `searching for a stop id should produce an accurate result`() {
        // arrange
        val searchQuery = "769"

        // act
        val searchResults = searchService.search(
            query = searchQuery,
            serviceLocations = serviceLocations
        ).blockingFirst()

        // assertx2
        assertThat(searchResults).isNotEmpty()
        assertThat(searchResults.first().id).isEqualTo("769")
        assertThat(searchResults.first().service).isEqualTo(Service.DUBLIN_BUS)
    }

    @Test
    fun `searching for unique locations should produce a few accurate results (1)`() {
        // arrange
        val searchQuery = "busaras"

        // act
        val searchResults = searchService.search(
            query = searchQuery,
            serviceLocations = serviceLocations
        ).blockingFirst()

        // assert
        assertThat(searchResults).hasSize(3)
        assertThat(searchResults.map { it.name }).containsExactly(
            "Busáras",
            "Dublin (Busaras)",
            "Dublin Busaras (Gate 15)"
        ).inOrder()
    }

    @Test
    fun `searching for unique locations should produce a few accurate results (2)`() {
        // arrange
        val searchQuery = "jervis"

        // act
        val searchResults = searchService.search(
            query = searchQuery,
            serviceLocations = serviceLocations
        ).blockingFirst()

        // assert
        assertThat(searchResults).hasSize(6)
        assertThat(searchResults.map { it.name }).containsExactly(
            "Jervis",
            "Jervis Street",
            "Ballydehob (Levis Bar)",
            "Dalys Cross (Jerrys Lounge Bar)",
            "Bangor Erris (Brogans Spar)",
            "Bangor Erris (Opp Brogans Spar)"
        ).inOrder()
    }

    @Test
    fun `searching for unique locations should produce a few accurate results (3)`() {
        // arrange
        val searchQuery = "GPO"

        // act
        val searchResults = searchService.search(
            query = searchQuery,
            serviceLocations = serviceLocations
        ).blockingFirst()

        // assert
        assertThat(searchResults).hasSize(1)
        assertThat(searchResults.first().name).isEqualTo("O'Connell - GPO")
    }

    @Test
    fun `searching for unique locations should produce a few accurate results (4)`() {
        // arrange
        val searchQuery = "UCD"

        // act
        val searchResults = searchService.search(
            query = searchQuery,
            serviceLocations = serviceLocations
        ).blockingFirst()

        // assert
        assertThat(searchResults).hasSize(10)
        assertThat(searchResults.map { it.name }).containsExactly(
            "UCD",
            "UCD",
            "UCD",
            "UCD",
            "UCD Campus",
            "UCD Smurfit",
            "UCD Smurfit",
            "UCD (Stillorgan Rd): entrance to UCD",
            "UCD: Montrose Student Residence",
            "Dublin (UCD Stillorgan Rd Flyover)"
        ).inOrder()
    }

    @Test
    fun `searching for unique locations should produce a few accurate results (5)`() {
        // arrange
        val searchQuery = "trinity"

        // act
        val searchResults = searchService.search(
            query = searchQuery,
            serviceLocations = serviceLocations
        ).blockingFirst()

        // assert
        assertThat(searchResults).hasSize(5)
        assertThat(searchResults.map { it.name }).containsExactly(
            "Trinity",
            "Dublin (Trinity College)",
            "Wexford (Trinity Street Centra)",
            "Dublin City Centre: Trinity College",
            "Wexford (Trinity St Opposite Centra)"
        ).inOrder()
    }

    @Test
    fun `searching for a route should produce accurate results (1)`() {
        // arrange
        val searchQuery = "46a"

        // act
        val searchResults = searchService.search(
            query = searchQuery,
            serviceLocations = serviceLocations
        ).blockingFirst()

        // assert
        assertThat(searchResults).isNotEmpty()
        assertThat(
            searchResults
                .take(25)
                .filterIsInstance<StopLocation>()
                .map { stopLocation ->
                    stopLocation.routeGroups.flatMap { routeGroup ->
                        routeGroup.routes
                    }
                }
                .all { routes -> routes.contains("46A") }
        ).isTrue()
    }

    @Test
    fun `searching for a route should produce accurate results (2)`() {
        // arrange
        val searchQuery = "100X"

        // act
        val searchResults = searchService.search(
            query = searchQuery,
            serviceLocations = serviceLocations
        ).blockingFirst()

        // assert
        assertThat(searchResults).isNotEmpty()
        assertThat(
            searchResults.take(10)
                .map { it.service }
                .filter { it == Service.BUS_EIREANN }
                .size
        ).isGreaterThan(5)
    }

    @Test
    fun `searching for a route should produce accurate results (3)`() {
        // arrange
        val searchQuery = "x2"

        // act
        val searchResults = searchService.search(
            query = searchQuery,
            serviceLocations = serviceLocations
        ).blockingFirst()

        // assertx2
        assertThat(searchResults).isNotEmpty()
        assertThat(
            searchResults.take(10)
                .map { it.service }
                .filter { it == Service.BUS_EIREANN }
                .size
        ).isGreaterThan(5)
    }

//    @Test
//    fun `searching for a luas route should produce accurate results (1)`() {
//        // arrange
//        val searchQuery = "green"
//
//        // act
//        val searchResults = searchService.search(
//            query = searchQuery,
//            serviceLocations = serviceLocations
//        ).blockingFirst()
//
//        // assert
//        assertThat(searchResults).isNotEmpty()
//        assertThat(
//            searchResults.take(30).all { it.service == Service.LUAS }
//        ).isTrue()
//    }
//
//    @Test
//    fun `searching for a luas route should produce accurate results (2)`() {
//        // arrange
//        val searchQuery = "red"
//
//        // act
//        val searchResults = searchService.search(
//            query = searchQuery,
//            serviceLocations = serviceLocations
//        ).blockingFirst()
//
//        // assert
//        assertThat(searchResults).isNotEmpty()
//        assertThat(searchResults.take(30).all { it.service == Service.LUAS }).isTrue()
//    }
}

package io.dublink.search

import com.google.common.truth.Truth.assertThat
import io.dublink.domain.model.DubLinkDockLocation
import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.model.DubLinkStopLocation
import io.dublink.test.createDubLinkDockLocation
import io.dublink.test.createDubLinkStopLocation
import io.reactivex.Single
import io.rtpi.api.DockLocation
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation
import io.rtpi.api.StopLocation
import io.rtpi.test.client.RtpiStaticDataClient
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(value = Parameterized::class)
class SearchServiceTest(
    private val query: String,
    private val expectedResults: List<DubLinkServiceLocation>
) {

    @Test
    fun `search should provide accurate results`() {
        // act
        val searchResults = searchService.search(
            query = query,
            serviceLocations = serviceLocations
        ).blockingFirst()

        // assert
        if (expectedResults.isEmpty()) {
            assertThat(searchResults).isEmpty()
        }
        expectedResults.forEachIndexed { index, expected ->
            val actual = searchResults[index]
            assertThat(actual::class.java).isEqualTo(expected::class.java)
            assertThat(actual.id).isEqualTo(expected.id)
            assertThat(actual.name).isEqualTo(expected.name)
            assertThat(actual.service).isEqualTo(expected.service)
        }
    }

    companion object {
        private val searchService = SearchService()
        private val serviceLocations = Single.zip(
            listOf(
                Service.LUAS,
                Service.IRISH_RAIL,
                Service.DUBLIN_BIKES,
                Service.DUBLIN_BUS,
                // Service.AIRCOACH,
                Service.BUS_EIREANN
            ).map { service ->
                RtpiStaticDataClient().getServiceLocations(service)
            }
        ) { serviceLocationStreams ->
            serviceLocationStreams
                .flatMap { it as List<ServiceLocation> }
                .map { serviceLocation ->
                    when (serviceLocation) {
                        is DockLocation -> DubLinkDockLocation(serviceLocation)
                        is StopLocation -> DubLinkStopLocation(serviceLocation)
                        else -> throw IllegalStateException()
                    }
                }
        }.blockingGet()

        @JvmStatic
        @Parameterized.Parameters(name = "{index}: query=\"{0}\"")
        fun data(): Iterable<Array<Any>> {
            return listOf(
                // specific query
                arrayOf(
                    "pearse dart",
                    listOf(
                        createDubLinkStopLocation(id = "PERSE", name = "Dublin Pearse", service = Service.IRISH_RAIL)
                    )
                ),
                arrayOf(
                    "rds",
                    listOf(
                        createDubLinkStopLocation(id = "486", name = "RDS Ballsbridge", service = Service.DUBLIN_BUS),
                        createDubLinkStopLocation(id = "136891", name = "Five Rds Of Skreen (Green Rd Nbound)", service = Service.BUS_EIREANN),
                        createDubLinkStopLocation(id = "137641", name = "Five Rds Of Skreen (Green Rd Sbound)", service = Service.BUS_EIREANN)
                    )
                ),
                arrayOf(
                    "rte",
                    listOf(
                        createDubLinkStopLocation(id = "762", name = "RTE", service = Service.DUBLIN_BUS),
                        createDubLinkStopLocation(id = "102191", name = "Dublin (RTE)", service = Service.BUS_EIREANN),
                        createDubLinkStopLocation(id = "106381", name = "Dublin (Opposite RTE)", service = Service.BUS_EIREANN)
                    )
                ),
                arrayOf(
                    "red cow",
                    listOf(
                        createDubLinkStopLocation(id = "LUAS6", name = "Red Cow", service = Service.LUAS),
                        createDubLinkStopLocation(id = "134491", name = "Red Cow LUAS", service = Service.BUS_EIREANN)
                    )
                ),
                arrayOf(
                    "busaras",
                    listOf(
                        createDubLinkStopLocation(id = "LUAS22", name = "Busáras", service = Service.LUAS),
                        createDubLinkStopLocation(id = "135001", name = "Dublin (Busaras)", service = Service.BUS_EIREANN),
                        createDubLinkStopLocation(id = "134961", name = "Dublin Busaras (Gate 15)", service = Service.BUS_EIREANN)
                    )
                ),
                arrayOf(
                    "jervis",
                    listOf(
                        createDubLinkStopLocation(id = "LUAS20", name = "Jervis", service = Service.LUAS),
                        createDubLinkDockLocation(id = "40", name = "Jervis Street", service = Service.DUBLIN_BIKES)
                    )
                ),
                arrayOf(
                    "gpo",
                    listOf(
                        createDubLinkStopLocation(id = "LUAS63", name = "O'Connell - GPO", service = Service.LUAS)
                    )
                ),
                arrayOf(
                    "ucd",
                    listOf(
                        createDubLinkStopLocation(id = "877", name = "UCD", service = Service.DUBLIN_BUS),
                        createDubLinkStopLocation(id = "4952", name = "UCD", service = Service.DUBLIN_BUS),
                        createDubLinkStopLocation(id = "4953", name = "UCD", service = Service.DUBLIN_BUS),
                        createDubLinkStopLocation(id = "765", name = "UCD", service = Service.DUBLIN_BUS),
                        createDubLinkStopLocation(id = "767", name = "UCD Campus", service = Service.DUBLIN_BUS),
                        createDubLinkStopLocation(id = "3166", name = "UCD Smurfit", service = Service.DUBLIN_BUS),
                        createDubLinkStopLocation(id = "3200", name = "UCD Smurfit", service = Service.DUBLIN_BUS)
                    )
                ),
                arrayOf(
                    "trinity",
                    listOf(
                        createDubLinkStopLocation(id = "LUAS60", name = "Trinity", service = Service.LUAS),
                        createDubLinkStopLocation(id = "105001", name = "Dublin (Trinity College)", service = Service.BUS_EIREANN),
                        createDubLinkStopLocation(id = "331621", name = "Wexford (Trinity Street Centra)", service = Service.BUS_EIREANN),
                        createDubLinkStopLocation(id = "300401", name = "Wexford (Trinity St Opposite Centra)", service = Service.BUS_EIREANN)
                    )
                ),
                arrayOf(
                    "quay",
                    listOf(
                        createDubLinkStopLocation(id = "139481", name = "Newry (Quays Shopping Centre)", service = Service.BUS_EIREANN),
                        createDubLinkDockLocation(id = "68", name = "Hanover Quay", service = Service.DUBLIN_BIKES),
                        createDubLinkDockLocation(id = "99", name = "City Quay", service = Service.DUBLIN_BIKES),
                        createDubLinkDockLocation(id = "16", name = "Georges Quay", service = Service.DUBLIN_BIKES),
                        createDubLinkStopLocation(id = "297", name = "Eden Quay", service = Service.DUBLIN_BUS),
                        createDubLinkStopLocation(id = "298", name = "Eden Quay", service = Service.DUBLIN_BUS),
                        createDubLinkStopLocation(id = "299", name = "Eden Quay", service = Service.DUBLIN_BUS)
                    )
                ),

                // stop ids
                arrayOf(
                    "769",
                    listOf(
                        createDubLinkStopLocation(id = "769", name = "Stillorgan Road", service = Service.DUBLIN_BUS)
                    )
                ),
                
                // autocomplete query
                arrayOf(
                    "da",
                    listOf(
                        createDubLinkStopLocation(id = "LUAS59", name = "Dawson", service = Service.LUAS),
                        createDubLinkStopLocation(id = "DLKEY", name = "Dalkey", service = Service.IRISH_RAIL),
                        createDubLinkDockLocation(id = "10", name = "Dame Street", service = Service.DUBLIN_BIKES),
                        createDubLinkStopLocation(id = "792", name = "Dawson Street", service = Service.DUBLIN_BUS),
                        createDubLinkStopLocation(id = "793", name = "Dawson Street", service = Service.DUBLIN_BUS),
                        createDubLinkStopLocation(id = "1025", name = "Dartry Road", service = Service.DUBLIN_BUS)
                    )
                ),
                arrayOf(
                    "che",
                    listOf(
                        createDubLinkStopLocation(id = "LUAS47", name = "Cherrywood", service = Service.LUAS),
                        createDubLinkStopLocation(id = "LUAS50", name = "Cheeverstown", service = Service.LUAS),
                        createDubLinkStopLocation(id = "CHORC", name = "Park West and Cherry Orchard", service = Service.IRISH_RAIL),
                        createDubLinkStopLocation(id = "850", name = "Chelmsford Road", service = Service.DUBLIN_BUS),
                        createDubLinkStopLocation(id = "851", name = "Chelmsford Road", service = Service.DUBLIN_BUS),
                        createDubLinkStopLocation(id = "903", name = "Chelmsford Road", service = Service.DUBLIN_BUS),
                        createDubLinkStopLocation(id = "2623", name = "Cheeverstown Rd", service = Service.DUBLIN_BUS)
                    )
                ),
                // TODO update static data
//                arrayOf(
//                    "chee",
//                    listOf(
//                        createDubLinkStopLocation(id = "LUAS50", name = "Cheeverstown", service = Service.LUAS),
//                        createDubLinkStopLocation(id = "2623", name = "Cheeverstown Rd", service = Service.DUBLIN_BUS),
//                        createDubLinkStopLocation(id = "2624", name = "Cheeverstown Rd", service = Service.DUBLIN_BUS),
//                        createDubLinkStopLocation(id = "2629", name = "Cheeverstown Rd", service = Service.DUBLIN_BUS),
//                        createDubLinkStopLocation(id = "2631", name = "Cheeverstown Rd", service = Service.DUBLIN_BUS),
//                        createDubLinkStopLocation(id = "6095", name = "Cheeverstown Rd", service = Service.DUBLIN_BUS),
//                        createDubLinkStopLocation(id = "7062", name = "Cheeverstown Rd", service = Service.DUBLIN_BUS)
//                    )
//                ),

                // typo query
                arrayOf(
                    "vlacktock",
                    listOf(
                        createDubLinkStopLocation(id = "BROCK", name = "Blackrock", service = Service.IRISH_RAIL),
                        createDubLinkStopLocation(id = "3033", name = "Blackrock", service = Service.DUBLIN_BUS),
                        createDubLinkStopLocation(id = "3085", name = "Blackrock", service = Service.DUBLIN_BUS),
                        createDubLinkStopLocation(id = "210431", name = "Blackrock Village", service = Service.BUS_EIREANN)
                    )
                ),

                // punctuation
                
                // general term
                arrayOf(
                    "dock",
                    listOf(
                        createDubLinkStopLocation(id = "LUAS54", name = "George's Dock", service = Service.LUAS),
                        createDubLinkStopLocation(id = "LUAS56", name = "Spencer Dock", service = Service.LUAS),
                        createDubLinkStopLocation(id = "GCDK", name = "Grand Canal Dock", service = Service.IRISH_RAIL),
                        createDubLinkDockLocation(id = "91", name = "South Dock Road", service = Service.DUBLIN_BIKES),
                        createDubLinkDockLocation(id = "69", name = "Grand Canal Dock", service = Service.DUBLIN_BIKES),
                        createDubLinkStopLocation(id = "245411", name = "Passage (Main St Victoria Dock)", service = Service.BUS_EIREANN)
                    )
                ),

                // service query
                arrayOf(
                    "go ahead",
                    listOf(
                        createDubLinkStopLocation(id = "124", name = "Virgin Mary Church", service = Service.DUBLIN_BUS),
                        createDubLinkStopLocation(id = "128", name = "Glasnevin Avenue", service = Service.DUBLIN_BUS),
                        createDubLinkStopLocation(id = "132", name = "Beneavin Park", service = Service.DUBLIN_BUS),
                        createDubLinkStopLocation(id = "139", name = "Grove Park Road", service = Service.DUBLIN_BUS),
                        createDubLinkStopLocation(id = "140", name = "Beneavin Park", service = Service.DUBLIN_BUS),
                        createDubLinkStopLocation(id = "430", name = "Sydney Avenue", service = Service.DUBLIN_BUS),
                        createDubLinkStopLocation(id = "431", name = "Cross Avenue", service = Service.DUBLIN_BUS)
                    )
                ),

                // jibberish
                arrayOf(
                    "pqtw",
                    emptyList<DubLinkServiceLocation>()
                )
            )
        }
    }

    /**
     * Tests to add
     * st. stephen's green
     * phibsborough luas
     * port
     * james's
     * bride's glen
     * george's dock
     * artane
     * st. john's
     */

//    @Test
//    fun `searching for a route should produce accurate results (1)`() {
//        // arrange
//        val searchQuery = "46a"
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
//            searchResults
//                .take(25)
//                .filterIsInstance<StopLocation>()
//                .map { stopLocation ->
//                    stopLocation.routeGroups.flatMap { routeGroup ->
//                        routeGroup.routes
//                    }
//                }
//                .all { routes -> routes.contains("46A") }
//        ).isTrue()
//    }
//
//    @Test
//    fun `searching for a route should produce accurate results (2)`() {
//        // arrange
//        val searchQuery = "100X"
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
//            searchResults.take(10)
//                .map { it.service }
//                .filter { it == Service.BUS_EIREANN }
//                .size
//        ).isGreaterThan(5)
//    }
//
//    @Test
//    fun `searching for a route should produce accurate results (3)`() {
//        // arrange
//        val searchQuery = "x2"
//
//        // act
//        val searchResults = searchService.search(
//            query = searchQuery,
//            serviceLocations = serviceLocations
//        ).blockingFirst()
//
//        // assertx2
//        assertThat(searchResults).isNotEmpty()
//        assertThat(
//            searchResults.take(10)
//                .map { it.service }
//                .filter { it == Service.BUS_EIREANN }
//                .size
//        ).isGreaterThan(5)
//    }
//
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

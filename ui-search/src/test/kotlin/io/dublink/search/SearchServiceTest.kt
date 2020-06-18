package io.dublink.search

import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import com.google.gson.Gson
import io.dublink.domain.model.DubLinkDockLocation
import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.model.DubLinkStopLocation
import io.dublink.test.createDubLinkStopLocation
import io.reactivex.Single
import io.rtpi.api.DockLocation
import io.rtpi.api.Operator
import io.rtpi.api.RouteGroup
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation
import io.rtpi.api.StopLocation
import io.rtpi.test.client.RtpiStaticDataClient
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(value = Parameterized::class)
class SearchServiceTest(
    private val queries: List<String>,
    private val expectedResults: List<DubLinkServiceLocation>
) {

    companion object {
        private val searchService = LuceneSearchService()
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
        @Parameterized.Parameters(name = "{index}: queries=\"{0}\"")
        fun data(): Iterable<Array<List<Any>>> {
            val testCaseInputJson = Gson().fromJson(
                SearchServiceTest::class.java.getResource("/search_data.json")!!.readText(),
                SearchJson::class.java
            )
            return testCaseInputJson.groups.flatMap { it.tests }.map { testCaseInput ->
                arrayOf(
                    testCaseInput.queries,
                    testCaseInput.expected.map { expected ->
                        createDubLinkStopLocation(id = expected.id, name = expected.name, service = expected.service)
                    }
                )
            }
        }
    }

    @Test
    fun `search should provide accurate results`() {
        for (query in queries) {
            val searchResults = searchService.search(
                query = query,
                serviceLocations = serviceLocations
            ).blockingFirst()

            if (expectedResults.isEmpty()) {
                assertThat(searchResults).isEmpty()
            }

            val mappedSearchResults = searchResults.map { searchResult ->
                SimpleServiceLocation(
                    id = searchResult.id,
                    name = searchResult.name,
                    service = searchResult.service
                )
            }.take(expectedResults.size)

            val mappedExpectedResults = expectedResults.map { expectedResult ->
                SimpleServiceLocation(
                    id = expectedResult.id,
                    name = expectedResult.name,
                    service = expectedResult.service
                )
            }

            assertWithMessage("query=\"$query\" failed")
                .that(mappedSearchResults)
                .containsExactlyElementsIn(mappedExpectedResults)
                .inOrder()
        }
    }
}

data class SearchJson(
    val groups: List<SearchGroupJson>
)

data class SearchGroupJson(
    val group: String,
    val tests: List<SearchTestJson>
)

data class SearchTestJson(
    val queries: List<String>,
    val expected: List<SimpleServiceLocation>
)

data class SimpleServiceLocation(
    val id: String,
    val name: String,
    val service: Service
)

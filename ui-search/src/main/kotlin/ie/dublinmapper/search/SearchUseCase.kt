package ie.dublinmapper.search

import ie.dublinmapper.domain.model.getName
import ie.dublinmapper.domain.repository.AggregatedServiceLocationRepository
import ie.dublinmapper.domain.repository.ServiceLocationResponse
import ie.dublinmapper.domain.service.LocationProvider
import ie.dublinmapper.domain.service.PermissionChecker
import ie.dublinmapper.domain.service.RxScheduler
import ie.dublinmapper.domain.util.haversine
import ie.dublinmapper.domain.util.truncateHead
import io.reactivex.Observable
import io.rtpi.api.*
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val serviceLocationRepository: AggregatedServiceLocationRepository,
    private val permissionChecker: PermissionChecker,
    private val locationProvider: LocationProvider,
    private val scheduler: RxScheduler
) {

    private val cache = mutableMapOf<String, SearchResultsResponse>()

    fun search(query: String): Observable<SearchResultsResponse> {
        if (query.isBlank()) {
            return Observable.just(
                SearchResultsResponse(
                    emptyList()
                )
            )
        }
        val cached = cache[query]
        if (cached != null) {
            return Observable.just(cached)
        }
        return serviceLocationRepository.get().map { response ->
            val result = search(
                query,
                response.filterIsInstance<ServiceLocationResponse.Data>()
                .flatMap { it.serviceLocations }
            )
            return@map SearchResultsResponse(result.take(50))

        }
    }

    private fun search(query: String, serviceLocations: List<ServiceLocation>): List<ServiceLocation> {
        val adaptedQuery = query.toLowerCase().trim()
        val searchResults = mutableListOf<ServiceLocation>()
        for (serviceLocation in serviceLocations) {
            if (serviceLocation.getName().toLowerCase().contains(adaptedQuery) ||
                serviceLocation.name.toLowerCase().contains(adaptedQuery) ||
                serviceLocation.id.toLowerCase().contains(adaptedQuery)) {
                searchResults.add(serviceLocation)
            } else {
                for (operator in serviceLocation.operators) {
                    if (operator.fullName.toLowerCase().contains(adaptedQuery)) {
                        searchResults.add(serviceLocation)
                    }
                }
            }
        }
        return searchResults
    }

    fun getNearbyServiceLocations(): Observable<NearbyLocationsResponse> {
        return if (permissionChecker.isLocationPermissionGranted()) {
            return Observable.concat(
                locationProvider.getLastKnownLocation(),
                locationProvider.getLocationUpdates()
            )
                .throttleLatest(30, TimeUnit.SECONDS)
                .flatMap { coordinate ->
                serviceLocationRepository
                    .get()
                    .map { responses ->
                        responses.filterIsInstance<ServiceLocationResponse.Data>()
                    }
                    .map { response ->
                        NearbyLocationsResponse.Data(
                            serviceLocations = response
                                .flatMap { it.serviceLocations }
                                .associateBy { it.coordinate.haversine(coordinate) }
                                .toSortedMap()
                                .truncateHead(5)
                        )
                    }
            }
        } else {
            Observable.just(NearbyLocationsResponse.LocationDisabled)
        }
    }

    fun getRecentSearches(): Observable<RecentSearchesResponse> {
        return Observable.just(
            RecentSearchesResponse(
                listOf(
                    IrishRailStation(
                        id = "12345",
                        name = "Platform 9 & 3/4",
                        coordinate = Coordinate(0.0, 0.0),
                        routes = listOf(Route("Dart", Operator.DART)),
                        operators = setOf(Operator.DART)
                    )
                )
            )
        )
    }
}

data class SearchResultsResponse(
    val serviceLocations: List<ServiceLocation>
)

sealed class NearbyLocationsResponse {

    data class Data(
        val serviceLocations: SortedMap<Double, ServiceLocation>
    ) : NearbyLocationsResponse()

    object LocationDisabled : NearbyLocationsResponse()
}

data class RecentSearchesResponse(
    val serviceLocations: List<ServiceLocation>
)

package ie.dublinmapper.search

import ie.dublinmapper.domain.model.RecentServiceLocationSearch
import ie.dublinmapper.domain.repository.AggregatedServiceLocationRepository
import ie.dublinmapper.domain.repository.RecentServiceLocationSearchRepository
import ie.dublinmapper.domain.repository.ServiceLocationKey
import ie.dublinmapper.domain.repository.ServiceLocationResponse
import ie.dublinmapper.domain.service.LocationProvider
import ie.dublinmapper.domain.service.PermissionChecker
import ie.dublinmapper.domain.util.haversine
import ie.dublinmapper.domain.util.truncateHead
import io.reactivex.Observable
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation
import java.time.Instant
import java.util.SortedMap
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val serviceLocationRepository: AggregatedServiceLocationRepository,
    private val recentSearchRepository: RecentServiceLocationSearchRepository,
    private val permissionChecker: PermissionChecker,
    private val locationProvider: LocationProvider
) {

    private val searchService = SearchService()

    fun search(query: String): Observable<SearchResultsResponse> =
        if (query.length < 2) {
            Observable.just(SearchResultsResponse(emptyList()))
        } else {
            serviceLocationRepository.get().flatMap { response ->
                searchService.search(
                    query = query,
                    serviceLocations = response
                        .filterIsInstance<ServiceLocationResponse.Data>()
                        .flatMap { it.serviceLocations }
                ).map { searchResults ->
                    SearchResultsResponse(searchResults.take(100))
                }
            }
        }

    fun getNearbyServiceLocations(): Observable<NearbyLocationsResponse> {
        return if (permissionChecker.isLocationPermissionGranted()) {
            return locationProvider.getLocationUpdates(thresholdDistance = 10.0)
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
        return recentSearchRepository
            .getRecentSearches()
            .map { recentSearches ->
                RecentSearchesResponse(
                    recentSearches.mapNotNull { recentSearch ->
                        getServiceLocation(recentSearch.service, recentSearch.locationId).blockingFirst().second
                    }
                )
            }
    }

    private fun getServiceLocation(
        service: Service,
        locationId: String
    ): Observable<Pair<Set<Service>, ServiceLocation?>> {
        return serviceLocationRepository.get(
            ServiceLocationKey(
                service = service,
                locationId = locationId
            )
        ).map<Pair<Set<Service>, ServiceLocation?>> { response ->
            when (response) {
                is ServiceLocationResponse.Data -> Pair(emptySet(), response.serviceLocations.first())
                is ServiceLocationResponse.Error -> Pair(setOf(response.service), null) // TODO exception ignored
            }
        }
    }

    private fun getServiceLocations(): Observable<Pair<Set<Service>, List<ServiceLocation>>> {
        return serviceLocationRepository.get()
            .map { responses ->
                val okResponses = responses.filterIsInstance<ServiceLocationResponse.Data>()
                val badResponses = responses.filterIsInstance<ServiceLocationResponse.Error>()
                Pair(
                    badResponses.map { it.service }.toSet(),
                    okResponses.flatMap { it.serviceLocations }
                )
            }
    }

    fun addRecentSearch(service: Service, locationId: String): Observable<Boolean> {
        return Observable.fromCallable {
            recentSearchRepository.saveRecentSearch(
                RecentServiceLocationSearch(service, locationId, Instant.now())
            )
            return@fromCallable true
        }
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

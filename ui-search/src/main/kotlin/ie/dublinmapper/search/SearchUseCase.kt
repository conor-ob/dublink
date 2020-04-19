package ie.dublinmapper.search

import ie.dublinmapper.domain.model.RecentServiceLocationSearch
import ie.dublinmapper.domain.repository.AggregatedServiceLocationRepository
import ie.dublinmapper.domain.repository.RecentServiceLocationSearchRepository
import ie.dublinmapper.domain.repository.ServiceLocationKey
import ie.dublinmapper.domain.repository.ServiceLocationResponse
import ie.dublinmapper.domain.service.LocationProvider
import ie.dublinmapper.domain.service.PermissionChecker
import ie.dublinmapper.domain.service.PreferenceStore
import ie.dublinmapper.domain.util.haversine
import ie.dublinmapper.domain.util.truncateHead
import io.reactivex.Observable
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation
import java.time.Instant
import java.util.SortedMap
import javax.inject.Inject

private const val maxSearchResults = 100
private const val maxRecentSearches = 50
private const val maxNearbyLocations = 50

class SearchUseCase @Inject constructor(
    private val serviceLocationRepository: AggregatedServiceLocationRepository,
    private val recentSearchRepository: RecentServiceLocationSearchRepository,
    private val permissionChecker: PermissionChecker,
    private val locationProvider: LocationProvider,
    private val preferenceStore: PreferenceStore
) {

    private val searchService = SearchService()

    fun search(query: String): Observable<SearchResultsResponse> =
        if (query.isEmpty()) {
            Observable.just(SearchResultsResponse.Empty)
        } else {
            serviceLocationRepository.get().flatMap { response ->
                searchService.search(
                    query = query,
                    serviceLocations = response
                        .filterIsInstance<ServiceLocationResponse.Data>()
                        .flatMap { it.serviceLocations }
                ).map { searchResults ->
                    if (searchResults.isEmpty()) {
                        SearchResultsResponse.NoResults(query)
                    } else {
                        SearchResultsResponse.Data(searchResults.take(maxSearchResults))
                    }
                }
            }
        }

    fun getNearbyServiceLocations(): Observable<NearbyLocationsResponse> {
        return when {
            !preferenceStore.isShowNearbyPlacesEnabled() -> {
                Observable.just(NearbyLocationsResponse.Hidden)
            }
            permissionChecker.isLocationPermissionGranted() -> {
                locationProvider.getLocationUpdates(thresholdDistance = 10.0)
                    .flatMap<NearbyLocationsResponse> { coordinate ->
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
                                        .truncateHead(maxNearbyLocations)
                                )
                            }
                    }
                    .startWith(NearbyLocationsResponse.Loading)
            }
            else -> {
                Observable.just(NearbyLocationsResponse.LocationDisabled)
            }
        }
    }

    fun getRecentSearches(): Observable<RecentSearchesResponse> {
        return if (preferenceStore.isShowRecentSearchesEnabled()) {
            recentSearchRepository
                .getRecentSearches()
                .map { recentSearches ->
                    if (recentSearches.isEmpty()) {
                        RecentSearchesResponse.Empty
                    } else {
                        RecentSearchesResponse.Data(
                            recentSearches.mapNotNull { recentSearch ->
                                getServiceLocation(recentSearch.service, recentSearch.locationId).blockingFirst().second
                            }.take(maxRecentSearches)
                        )
                    }
                }
        } else {
            Observable.just(RecentSearchesResponse.Hidden)
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

    fun clearRecentSearches(): Observable<Boolean> {
        return Observable.fromCallable {
            recentSearchRepository.clearRecentSearches()
            return@fromCallable true
        }
    }
}

sealed class SearchResultsResponse {

    data class Data(
        val serviceLocations: List<ServiceLocation>
    ) : SearchResultsResponse()

    data class NoResults(
        val query: String
    ) : SearchResultsResponse()

    object Empty : SearchResultsResponse()
}

sealed class NearbyLocationsResponse {

    data class Data(
        val serviceLocations: SortedMap<Double, ServiceLocation>
    ) : NearbyLocationsResponse()

    object LocationDisabled : NearbyLocationsResponse()

    object Loading : NearbyLocationsResponse()

    object Hidden : NearbyLocationsResponse()
}

sealed class RecentSearchesResponse {

    data class Data(
        val serviceLocations: List<ServiceLocation>
    ) : RecentSearchesResponse()

    object Empty : RecentSearchesResponse()

    object Hidden : RecentSearchesResponse()
}

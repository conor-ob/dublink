package io.dublink.search

import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.model.RecentServiceLocationSearch
import io.dublink.domain.repository.AggregatedServiceLocationRepository
import io.dublink.domain.repository.RecentServiceLocationSearchRepository
import io.dublink.domain.repository.ServiceLocationKey
import io.dublink.domain.repository.ServiceLocationResponse
import io.dublink.domain.service.LocationProvider
import io.dublink.domain.service.PermissionChecker
import io.dublink.domain.service.PreferenceStore
import io.dublink.domain.util.AppConstants
import io.dublink.domain.util.haversine
import io.dublink.domain.util.truncateHead
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
    private val locationProvider: LocationProvider,
    private val preferenceStore: PreferenceStore
) {

    private val searchService = SearchService()

    fun search(query: String): Observable<SearchResultsResponse> =
        if (query.isEmpty()) {
            Observable.just(SearchResultsResponse.Empty)
        } else {
            serviceLocationRepository.get()
                .filter { response ->
                    response
                        .filterIsInstance<ServiceLocationResponse.Data>()
                        .flatMap { it.serviceLocations }
                        .isNotEmpty()
                }
                .flatMap { response ->
                    searchService.search(
                        query = query,
                        serviceLocations = response
                            .filterIsInstance<ServiceLocationResponse.Data>()
                            .flatMap { it.serviceLocations }
                ).map { searchResults ->
                    if (searchResults.isEmpty()) {
                        SearchResultsResponse.NoResults(query)
                    } else {
                        SearchResultsResponse.Data(
                            searchResults.take(
                                AppConstants.maxSearchResults
                            )
                        )
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
                            .stream()
                            .map { responses ->
                                responses.filterIsInstance<ServiceLocationResponse.Data>()
                            }
                            .map { response ->
                                NearbyLocationsResponse.Data(
                                    serviceLocations = response
                                        .flatMap { it.serviceLocations }
                                        .associateBy { it.coordinate.haversine(coordinate) }
                                        .toSortedMap()
                                        .truncateHead(AppConstants.maxNearbyLocations)
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
                            }.take(AppConstants.maxRecentSearches)
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
    ): Observable<Pair<Set<Service>, DubLinkServiceLocation?>> {
        return serviceLocationRepository.get(
            ServiceLocationKey(
                service = service,
                locationId = locationId
            )
        ).map<Pair<Set<Service>, DubLinkServiceLocation?>> { response ->
            when (response) {
                is ServiceLocationResponse.Data -> Pair(emptySet(), response.serviceLocations.first())
                is ServiceLocationResponse.Error -> Pair(setOf(response.service), null) // TODO exception ignored
            }
        }
    }

    private fun getServiceLocations(): Observable<Pair<Set<Service>, List<ServiceLocation>>> {
        return serviceLocationRepository.stream()
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
        val serviceLocations: List<DubLinkServiceLocation>
    ) : SearchResultsResponse()

    data class NoResults(
        val query: String
    ) : SearchResultsResponse()

    object Empty : SearchResultsResponse()
}

sealed class NearbyLocationsResponse {

    data class Data(
        val serviceLocations: SortedMap<Double, DubLinkServiceLocation>
    ) : NearbyLocationsResponse()

    object LocationDisabled : NearbyLocationsResponse()

    object Loading : NearbyLocationsResponse()

    object Hidden : NearbyLocationsResponse()
}

sealed class RecentSearchesResponse {

    data class Data(
        val serviceLocations: List<DubLinkServiceLocation>
    ) : RecentSearchesResponse()

    object Empty : RecentSearchesResponse()

    object Hidden : RecentSearchesResponse()
}

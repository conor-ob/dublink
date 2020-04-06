package ie.dublinmapper.search

import ie.dublinmapper.domain.model.getName
import ie.dublinmapper.domain.repository.AggregatedServiceLocationRepository
import ie.dublinmapper.domain.repository.ServiceLocationRepository
import ie.dublinmapper.domain.service.PermissionChecker
import ie.dublinmapper.domain.service.RxScheduler
import io.reactivex.Observable
import io.rtpi.api.ServiceLocation
import javax.inject.Inject
import javax.inject.Named

class SearchUseCase @Inject constructor(
    private val serviceLocationRepository: AggregatedServiceLocationRepository,
//    private val nearbyUseCase: NearbyUseCase,
    private val permissionChecker: PermissionChecker,
    private val scheduler: RxScheduler
) {

    private val cache = mutableMapOf<String, SearchResponse>()

    fun search(query: String): Observable<SearchResponse> {
        if (query.isBlank()) {
            return Observable.just(
                SearchResponse(
                    emptyList()
                )
            )
        }
        val cached = cache[query]
        if (cached != null) {
            return Observable.just(cached)
        }
//        return serviceLocationRepository.get().map {
//            SearchResponse(
//                search(query, it).take(50)
//            )
//        }
        TODO()
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

//    fun getNearbyServiceLocations(): Observable<NearbyResponse> {
//        return if (permissionChecker.isLocationPermissionGranted()) {
//            nearbyUseCase.getNearbyServiceLocations()
//        } else {
//            Observable.just(NearbyResponse(sortedMapOf()))
//        }
//    }
}

data class SearchResponse(
    val serviceLocations: List<ServiceLocation>
)

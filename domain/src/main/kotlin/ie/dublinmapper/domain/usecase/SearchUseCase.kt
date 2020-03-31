package ie.dublinmapper.domain.usecase

import ie.dublinmapper.domain.model.getName
import ie.dublinmapper.domain.repository.LocationRepository
import ie.dublinmapper.domain.service.RxScheduler
import io.reactivex.Observable
import io.rtpi.api.ServiceLocation
import javax.inject.Inject
import javax.inject.Named

class SearchUseCase @Inject constructor(
    @Named("SERVICE_LOCATION") private val locationRepository: LocationRepository,
    private val scheduler: RxScheduler
) {

    private val cache = mutableMapOf<String, SearchResponse>()

    fun search(query: String): Observable<SearchResponse> {
        val cached = cache[query]
        if (cached != null) {
            return Observable.just(cached)
        }
        return locationRepository.get().map { SearchResponse(search(query, it).take(50)) }
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
}

data class SearchResponse(
    val serviceLocations: List<ServiceLocation>
)

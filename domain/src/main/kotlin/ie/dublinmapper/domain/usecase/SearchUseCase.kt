package ie.dublinmapper.domain.usecase

import ie.dublinmapper.domain.model.getName
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.core.RxScheduler
import io.reactivex.Observable
import io.reactivex.functions.Function6
import io.rtpi.api.*
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val aircoachStopRepository: Repository<AircoachStop>,
    private val busEireannStopRepository: Repository<BusEireannStop>,
    private val irishRailStationRepository: Repository<IrishRailStation>,
    private val dublinBikesDockRepository: Repository<DublinBikesDock>,
    private val dublinBusStopRepository: Repository<DublinBusStop>,
    private val luasStopRepository: Repository<LuasStop>,
    private val scheduler: RxScheduler
) {

    private val cache = mutableMapOf<String, SearchResponse>()

    fun search(query: String): Observable<SearchResponse> {
        val cached = cache[query]
        if (cached != null) {
            return Observable.just(cached)
        }
        return Observable.combineLatest(
            aircoachStopRepository.getAll().subscribeOn(scheduler.io),
            busEireannStopRepository.getAll().subscribeOn(scheduler.io),
            irishRailStationRepository.getAll().subscribeOn(scheduler.io),
            dublinBikesDockRepository.getAll().subscribeOn(scheduler.io),
            dublinBusStopRepository.getAll().subscribeOn(scheduler.io),
            luasStopRepository.getAll().subscribeOn(scheduler.io),
            Function6 { aircoachStops, busEireannStops, irishRailStations, dublinBikesDocks, dublinBusStops, luasStops ->
                search(query, aircoachStops, busEireannStops, irishRailStations, dublinBikesDocks, dublinBusStops, luasStops)
            }
        )
    }

    private fun search(
        query: String,
        aircoachStops: List<AircoachStop>,
        busEireannStops: List<BusEireannStop>,
        irishRailStations: List<IrishRailStation>,
        dublinBikesDocks: List<DublinBikesDock>,
        dublinBusStops: List<DublinBusStop>,
        luasStops: List<LuasStop>
    ) : SearchResponse {
        val searchCollections = mutableListOf<Collection<ServiceLocation>>()
        searchCollections.add(search(query, aircoachStops))
        searchCollections.add(search(query, busEireannStops))
        searchCollections.add(search(query, dublinBikesDocks))
        searchCollections.add(search(query, dublinBusStops))
        searchCollections.add(search(query, irishRailStations))
        searchCollections.add(search(query, luasStops))
        searchCollections.sortBy { it.size }
        val response = SearchResponse(searchCollections.flatten().take(50))
        cache[query] = response
        return response
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

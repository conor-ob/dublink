package ie.dublinmapper.domain.usecase

import ie.dublinmapper.domain.model.*
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.util.Thread
import io.reactivex.Observable
import io.reactivex.functions.Function7
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val aircoachStopRepository: Repository<AircoachStop>,
    private val busEireannStopRepository: Repository<BusEireannStop>,
    private val dartStationRepository: Repository<DartStation>,
    private val dublinBikesDockRepository: Repository<DublinBikesDock>,
    private val dublinBusStopRepository: Repository<DublinBusStop>,
    private val luasStopRepository: Repository<LuasStop>,
    private val swordsExpressStopRepository: Repository<SwordsExpressStop>,
    private val thread: Thread
) {

    fun search(query: String): Observable<List<ServiceLocation>> {
        return Observable.combineLatest(
            aircoachStopRepository.getAll().subscribeOn(thread.io),
            busEireannStopRepository.getAll().subscribeOn(thread.io),
            dartStationRepository.getAll().subscribeOn(thread.io),
            dublinBikesDockRepository.getAll().subscribeOn(thread.io),
            dublinBusStopRepository.getAll().subscribeOn(thread.io),
            luasStopRepository.getAll().subscribeOn(thread.io),
            swordsExpressStopRepository.getAll().subscribeOn(thread.io),
            Function7 { aircoachStops, busEireannStops, dartStations, dublinBikesDocks, dublinBusStops, luasStops, swordsExpressStops ->
                search(query, aircoachStops, busEireannStops, dartStations, dublinBikesDocks, dublinBusStops, luasStops, swordsExpressStops)
            }
        )
    }

    private fun search(
        query: String,
        aircoachStops: List<AircoachStop>,
        busEireannStops: List<BusEireannStop>,
        dartStations: List<DartStation>,
        dublinBikesDocks: List<DublinBikesDock>,
        dublinBusStops: List<DublinBusStop>,
        luasStops: List<LuasStop>,
        swordsExpressStops: List<SwordsExpressStop>
    ) : List<ServiceLocation> {
        val searchCollections = mutableListOf<Collection<ServiceLocation>>()
        searchCollections.add(search(query, aircoachStops))
        searchCollections.add(search(query, busEireannStops))
        searchCollections.add(search(query, dartStations))
        searchCollections.add(search(query, dublinBikesDocks))
        searchCollections.add(search(query, dublinBusStops))
        searchCollections.add(search(query, luasStops))
        searchCollections.add(search(query, swordsExpressStops))
        searchCollections.sortBy { it.size }
        return searchCollections.flatten()
    }

    private fun search(query: String, serviceLocations: List<ServiceLocation>): List<ServiceLocation> {
        val adaptedQuery = query.toLowerCase().trim()
        val searchResults = mutableListOf<ServiceLocation>()
        for (serviceLocation in serviceLocations) {
            if (serviceLocation.name.toLowerCase().contains(adaptedQuery) ||
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

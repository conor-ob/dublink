package ie.dublinmapper.domain.usecase

import ie.dublinmapper.domain.model.*
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.util.Thread
import io.reactivex.Observable
import io.reactivex.functions.Function4
import java.util.*
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val dartStationRepository: Repository<DartStation>,
    private val dublinBikesDockRepository: Repository<DublinBikesDock>,
    private val dublinBusStopRepository: Repository<DublinBusStop>,
    private val luasStopRepository: Repository<LuasStop>,
    private val thread: Thread
) {
    fun search(query: String): Observable<List<ServiceLocation>> {
        return Observable.combineLatest(
            dartStationRepository.getAll().subscribeOn(thread.io),
            dublinBikesDockRepository.getAll().subscribeOn(thread.io),
            dublinBusStopRepository.getAll().subscribeOn(thread.io),
            luasStopRepository.getAll().subscribeOn(thread.io),
            Function4 { dartStations, dublinBikesDocks, dublinBusStops, luasStops ->
                search(query, dartStations, dublinBikesDocks, dublinBusStops, luasStops)
            }
        )
    }

    private fun search(
        query: String,
        dartStations: List<DartStation>,
        dublinBikesDocks: List<DublinBikesDock>,
        dublinBusStops: List<DublinBusStop>,
        luasStops: List<LuasStop>
    ) : List<ServiceLocation> {
        val searchResults = mutableListOf<ServiceLocation>()
        val dartStationMatches = search(query, dartStations)
        val dublinBikesDockMatches = search(query, dublinBikesDocks)
        val dublinBusStopMatches = search(query, dublinBusStops)
        val luasStopMatches = search(query, luasStops)
        val sortedMap = TreeMap<Int, List<ServiceLocation>>()
        sortedMap[dartStationMatches.size] = dartStationMatches
        sortedMap[dublinBikesDockMatches.size] = dublinBikesDockMatches
        sortedMap[dublinBusStopMatches.size] = dublinBusStopMatches
        sortedMap[luasStopMatches.size] = luasStopMatches
        val iterator = sortedMap.entries.iterator()
        while (iterator.hasNext()) {
            searchResults.addAll(iterator.next().value)
        }
        return searchResults
    }

    private fun search(query: String, serviceLocations: List<ServiceLocation>): List<ServiceLocation> {
        val adaptedQuery = query.toLowerCase().trim()
        val searchResults = mutableListOf<ServiceLocation>()
        for (serviceLocation in serviceLocations) {
            if (serviceLocation.name.toLowerCase().contains(adaptedQuery) ||
                    serviceLocation.id.toLowerCase().contains(adaptedQuery)) {
                searchResults.add(serviceLocation)
            }
        }
        return searchResults
    }

}

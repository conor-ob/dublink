package ie.dublinmapper.domain.usecase

import ie.dublinmapper.domain.model.*
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.util.EnabledServiceManager
import ie.dublinmapper.util.RxScheduler
import io.reactivex.Observable
import io.reactivex.functions.Function6
import io.rtpi.api.Service
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val aircoachStopRepository: Repository<DetailedAircoachStop>,
    private val busEireannStopRepository: Repository<DetailedBusEireannStop>,
    private val irishRailStationRepository: Repository<DetailedIrishRailStation>,
    private val dublinBikesDockRepository: Repository<DetailedDublinBikesDock>,
    private val dublinBusStopRepository: Repository<DetailedDublinBusStop>,
    private val luasStopRepository: Repository<DetailedLuasStop>,
    private val scheduler: RxScheduler,
    private val enabledServiceManager: EnabledServiceManager
) {

    fun search(query: String): Observable<SearchResponse> {
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
        aircoachStops: List<DetailedAircoachStop>,
        busEireannStops: List<DetailedBusEireannStop>,
        irishRailStations: List<DetailedIrishRailStation>,
        dublinBikesDocks: List<DetailedDublinBikesDock>,
        dublinBusStops: List<DetailedDublinBusStop>,
        luasStops: List<DetailedLuasStop>
    ) : SearchResponse {
        val searchCollections = mutableListOf<Collection<DetailedServiceLocation>>()
        if (enabledServiceManager.isServiceEnabled(Service.AIRCOACH)) {
            searchCollections.add(search(query, aircoachStops))
        }
        if (enabledServiceManager.isServiceEnabled(Service.BUS_EIREANN)) {
            searchCollections.add(search(query, busEireannStops))
        }
        if (enabledServiceManager.isServiceEnabled(Service.DUBLIN_BIKES)) {
            searchCollections.add(search(query, dublinBikesDocks))
        }
        if (enabledServiceManager.isServiceEnabled(Service.DUBLIN_BUS)) {
            searchCollections.add(search(query, dublinBusStops))
        }
        if (enabledServiceManager.isServiceEnabled(Service.IRISH_RAIL)) {
            searchCollections.add(search(query, irishRailStations))
        }
        if (enabledServiceManager.isServiceEnabled(Service.LUAS)) {
            searchCollections.add(search(query, luasStops))
        }
        searchCollections.sortBy { it.size }
        return SearchResponse(searchCollections.flatten().take(50))
    }

    private fun search(query: String, serviceLocations: List<DetailedServiceLocation>): List<DetailedServiceLocation> {
        val adaptedQuery = query.toLowerCase().trim()
        val searchResults = mutableListOf<DetailedServiceLocation>()
        for (serviceLocation in serviceLocations) {
            if (serviceLocation.serviceLocationName.toLowerCase().contains(adaptedQuery) ||
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
    val serviceLocations: List<DetailedServiceLocation>
)

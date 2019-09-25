package ie.dublinmapper.domain.usecase

import ie.dublinmapper.domain.model.*
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.util.EnabledServiceManager
import ie.dublinmapper.util.RxScheduler
import ie.dublinmapper.util.Service
import io.reactivex.Observable
import io.reactivex.functions.Function6
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val aircoachStopRepository: Repository<AircoachStop>,
    private val busEireannStopRepository: Repository<BusEireannStop>,
    private val irishRailStationRepository: Repository<IrishRailStation>,
    private val dublinBikesDockRepository: Repository<DublinBikesDock>,
    private val dublinBusStopRepository: Repository<DublinBusStop>,
    private val luasStopRepository: Repository<LuasStop>,
    private val swordsExpressStopRepository: Repository<SwordsExpressStop>,
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
//            swordsExpressStopRepository.getAll().subscribeOn(scheduler.io),
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

    private fun search(query: String, serviceLocations: List<ServiceLocation>): List<ServiceLocation> {
        val adaptedQuery = query.toLowerCase().trim()
        val searchResults = mutableListOf<ServiceLocation>()
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
    val serviceLocations: List<ServiceLocation>
)

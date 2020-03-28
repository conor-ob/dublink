package ie.dublinmapper.domain.usecase

import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.domain.service.LocationProvider
import ie.dublinmapper.domain.service.RxScheduler
import ie.dublinmapper.domain.util.haversine
import ie.dublinmapper.domain.util.truncateHead
import io.reactivex.functions.Function6
import io.reactivex.Observable
import io.rtpi.api.*
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NearbyUseCase @Inject constructor(
    private val aircoachStopRepository: Repository<AircoachStop>,
    private val busEireannStopRepository: Repository<BusEireannStop>,
    private val irishRailStationRepository: Repository<IrishRailStation>,
    private val dublinBikesDockRepository: Repository<DublinBikesDock>,
    private val dublinBusStopRepository: Repository<DublinBusStop>,
    private val luasStopRepository: Repository<LuasStop>,
    private val scheduler: RxScheduler,
    private val locationProvider: LocationProvider
) {

    fun getNearbyServiceLocation(): Observable<NearbyResponse> {
        return Observable.concat(
            locationProvider.getLastKnownLocation(),
            locationProvider.getLocationUpdates()
        )
            .throttleFirst(30, TimeUnit.SECONDS)
            .flatMap { filterNearby(it) }
    }

    private fun filterNearby(coordinate: Coordinate): Observable<NearbyResponse> {
        return Observable.combineLatest(
            aircoachStopRepository.getAll().subscribeOn(scheduler.io),
            busEireannStopRepository.getAll().subscribeOn(scheduler.io),
            irishRailStationRepository.getAll().subscribeOn(scheduler.io),
            dublinBikesDockRepository.getAll().subscribeOn(scheduler.io),
            dublinBusStopRepository.getAll().subscribeOn(scheduler.io),
            luasStopRepository.getAll().subscribeOn(scheduler.io),
            Function6 { aircoachStops, busEireannStops, irishRailStations, dublinBikesDocks, dublinBusStops, luasStops ->
                filterBlah(coordinate, aircoachStops, busEireannStops, irishRailStations, dublinBikesDocks, dublinBusStops, luasStops)
            }
        )
//        return Observable.combineLatest(
//            aircoachStopRepository.getAll().subscribeOn(scheduler.io).startWith(emptyList<AircoachStop>()),
//            busEireannStopRepository.getAll().subscribeOn(scheduler.io).startWith(emptyList<BusEireannStop>()),
//            irishRailStationRepository.getAll().subscribeOn(scheduler.io).startWith(emptyList<IrishRailStation>()),
//            dublinBikesDockRepository.getAll().subscribeOn(scheduler.io).startWith(emptyList<DublinBikesDock>()),
//            dublinBusStopRepository.getAll().subscribeOn(scheduler.io).startWith(emptyList<DublinBusStop>()),
//            luasStopRepository.getAll().subscribeOn(scheduler.io).startWith(emptyList<LuasStop>()),
//            Function6 { aircoachStops, busEireannStops, irishRailStations, dublinBikesDocks, dublinBusStops, luasStops ->
//                filterBlah(coordinate, aircoachStops, busEireannStops, irishRailStations, dublinBikesDocks, dublinBusStops, luasStops)
//            }
//        )
    }

    private fun filterBlah(
        coordinate: Coordinate,
        aircoachStops: List<AircoachStop>,
        busEireannStops: List<BusEireannStop>,
        irishRailStations: List<IrishRailStation>,
        dublinBikesDocks: List<DublinBikesDock>,
        dublinBusStops: List<DublinBusStop>,
        luasStops: List<LuasStop>
    ): NearbyResponse {
        val results = mutableListOf<ServiceLocation>()
        results.addAll(aircoachStops)
        results.addAll(busEireannStops)
        results.addAll(dublinBikesDocks)
        results.addAll(dublinBusStops)
        results.addAll(irishRailStations)
        results.addAll(luasStops)
        return NearbyResponse(
            results
                .associateBy { coordinate.haversine(it.coordinate) }
                .toSortedMap()
                .truncateHead(50)
        )
    }
}

data class NearbyResponse(
    val serviceLocations: SortedMap<Double, ServiceLocation>
)

//class NearbyUseCase @Inject constructor(
//    private val aircoachStopRepository: Repository<AircoachStop>,
//    private val busEireannStopRepository: Repository<BusEireannStop>,
//    private val irishRailStationRepository: Repository<IrishRailStation>,
//    private val dublinBikesDockRepository: Repository<DublinBikesDock>,
//    private val dublinBusStopRepository: Repository<DublinBusStop>,
//    private val luasStopRepository: Repository<LuasStop>,
//    private val scheduler: RxScheduler
//) {
//
//    fun getNearbyServiceLocations(coordinate: Coordinate): Observable<Response> {
//        return Observable.combineLatest(
//            aircoachStopRepository.getAll().startWith(emptyList<AircoachStop>()).subscribeOn(scheduler.io),
//            busEireannStopRepository.getAll().startWith(emptyList<BusEireannStop>()).subscribeOn(scheduler.io),
//            irishRailStationRepository.getAll().startWith(emptyList<IrishRailStation>()).subscribeOn(scheduler.io),
//            dublinBikesDockRepository.getAll().startWith(emptyList<DublinBikesDock>()).subscribeOn(scheduler.io),
//            dublinBusStopRepository.getAll().startWith(emptyList<DublinBusStop>()).subscribeOn(scheduler.io),
//            luasStopRepository.getAll().startWith(emptyList<LuasStop>()).subscribeOn(scheduler.io),
//            Function7 { aircoachStops, busEireannStops, irishRailStations, dublinBikesDocks, dublinBusStops, luasStops ->
//                filter(coordinate, aircoachStops, busEireannStops, irishRailStations, dublinBikesDocks, dublinBusStops, luasStops) }
//        )
//    }
//
//    private fun filter(
//        coordinate: Coordinate,
//        aircoachStops: List<AircoachStop>,
//        busEireannStops: List<BusEireannStop>,
//        irishRailStations: List<IrishRailStation>,
//        dublinBikesDocks: List<DublinBikesDock>,
//        dublinBusStops: List<DublinBusStop>,
//        luasStops: List<LuasStop>
//    ): Response {
//        val serviceLocations = mutableListOf<ServiceLocation>()
//        serviceLocations.addAll(aircoachStops)
//        serviceLocations.addAll(busEireannStops)
//        serviceLocations.addAll(irishRailStations)
//        serviceLocations.addAll(dublinBikesDocks)
//        serviceLocations.addAll(dublinBusStops)
//        serviceLocations.addAll(luasStops)
////        val sorted = serviceLocations.associateTo(TreeMap()) { serviceLocation ->
////            LocationUtils.haversineDistance(coordinate, serviceLocation.coordinate) to serviceLocation }
//        val sorted = TreeMap<Double, ServiceLocation>()
//        for (serviceLocation in serviceLocations) {
//            sorted[LocationUtils.haversineDistance(coordinate, serviceLocation.coordinate)] = serviceLocation
//        }
//        val result = mutableListOf<ServiceLocation>()
//        result.addAll(findFirstN(3, Operator.rail(), sorted))
//        result.addAll(findFirstN(7, Operator.bike(), sorted))
//        result.addAll(findFirstN(15, Operator.bus(), sorted))
//        result.addAll(findFirstN(5, Operator.tram(), sorted))
//        val isComplete = aircoachStops.isNotEmpty() && busEireannStops.isNotEmpty() && irishRailStations.isNotEmpty() &&
//                dublinBikesDocks.isNotEmpty() && dublinBusStops.isNotEmpty() && luasStops.isNotEmpty()
//        return Response(result, isComplete)
//    }
//
//    private fun findFirstN(
//        limit: Int,
//        operators: EnumSet<Operator>,
//        serviceLocations: TreeMap<Double, ServiceLocation>
//    ): List<ServiceLocation> {
//        var count = 0
//        val result = mutableListOf<ServiceLocation>()
//        for ((_, value) in serviceLocations) {
//            if (count >= limit) {
//                break
//            }
//            if (operators.intersectWith(value.operators)) {
//                result.add(value)
//                count++
//            }
//        }
//        return result
//    }
//
//}
//
//data class Response(
//    val serviceLocations: List<ServiceLocation>,
//    val isComplete: Boolean
//)

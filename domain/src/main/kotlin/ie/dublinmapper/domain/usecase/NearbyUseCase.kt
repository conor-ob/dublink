package ie.dublinmapper.domain.usecase

import ie.dublinmapper.domain.model.*
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.util.*
import io.reactivex.Observable
import io.reactivex.functions.Function7
import io.rtpi.api.Operator
import java.util.*
import javax.inject.Inject

//class NearbyUseCase @Inject constructor(
//    private val aircoachStopRepository: Repository<DetailedAircoachStop>,
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
//            aircoachStopRepository.getAll().startWith(emptyList<DetailedAircoachStop>()).subscribeOn(scheduler.io),
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
//        aircoachStops: List<DetailedAircoachStop>,
//        busEireannStops: List<BusEireannStop>,
//        irishRailStations: List<IrishRailStation>,
//        dublinBikesDocks: List<DublinBikesDock>,
//        dublinBusStops: List<DublinBusStop>,
//        luasStops: List<DetailedLuasStop>
//    ): Response {
//        val serviceLocations = mutableListOf<DetailedServiceLocation>()
//        serviceLocations.addAll(aircoachStops)
//        serviceLocations.addAll(busEireannStops)
//        serviceLocations.addAll(irishRailStations)
//        serviceLocations.addAll(dublinBikesDocks)
//        serviceLocations.addAll(dublinBusStops)
//        serviceLocations.addAll(luasStops)
////        val sorted = serviceLocations.associateTo(TreeMap()) { serviceLocation ->
////            LocationUtils.haversineDistance(coordinate, serviceLocation.coordinate) to serviceLocation }
//        val sorted = TreeMap<Double, DetailedServiceLocation>()
//        for (serviceLocation in serviceLocations) {
//            sorted[LocationUtils.haversineDistance(coordinate, serviceLocation.coordinate)] = serviceLocation
//        }
//        val result = mutableListOf<DetailedServiceLocation>()
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
//        serviceLocations: TreeMap<Double, DetailedServiceLocation>
//    ): List<DetailedServiceLocation> {
//        var count = 0
//        val result = mutableListOf<DetailedServiceLocation>()
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
//    val serviceLocations: List<DetailedServiceLocation>,
//    val isComplete: Boolean
//)

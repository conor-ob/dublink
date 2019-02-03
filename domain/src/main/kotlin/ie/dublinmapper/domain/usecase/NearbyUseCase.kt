package ie.dublinmapper.domain.usecase

import ie.dublinmapper.domain.model.*
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.util.*
import io.reactivex.Observable
import io.reactivex.functions.Function7
import java.util.*
import javax.inject.Inject

class NearbyUseCase @Inject constructor(
    private val aircoachStopRepository: Repository<AircoachStop>,
    private val busEireannStopRepository: Repository<BusEireannStop>,
    private val dartStationRepository: Repository<DartStation>,
    private val dublinBikesDockRepository: Repository<DublinBikesDock>,
    private val dublinBusStopRepository: Repository<DublinBusStop>,
    private val luasStopRepository: Repository<LuasStop>,
    private val swordsExpressStopRepository: Repository<SwordsExpressStop>,
    private val thread: Thread
) {

    fun getNearbyServiceLocations(coordinate: Coordinate): Observable<Response> {
        return Observable.combineLatest(
            aircoachStopRepository.getAll().startWith(emptyList<AircoachStop>()).subscribeOn(thread.io),
            busEireannStopRepository.getAll().startWith(emptyList<BusEireannStop>()).subscribeOn(thread.io),
            dartStationRepository.getAll().startWith(emptyList<DartStation>()).subscribeOn(thread.io),
            dublinBikesDockRepository.getAll().startWith(emptyList<DublinBikesDock>()).subscribeOn(thread.io),
            dublinBusStopRepository.getAll().startWith(emptyList<DublinBusStop>()).subscribeOn(thread.io),
            luasStopRepository.getAll().startWith(emptyList<LuasStop>()).subscribeOn(thread.io),
            swordsExpressStopRepository.getAll().startWith(emptyList<SwordsExpressStop>()).subscribeOn(thread.io),
            Function7 { aircoachStops, busEireannStops, dartStations, dublinBikesDocks, dublinBusStops, luasStops, swordsExpressStops ->
                filter(coordinate, aircoachStops, busEireannStops, dartStations, dublinBikesDocks, dublinBusStops, luasStops, swordsExpressStops) }
        )
    }

    private fun filter(
        coordinate: Coordinate,
        aircoachStops: List<AircoachStop>,
        busEireannStops: List<BusEireannStop>,
        dartStations: List<DartStation>,
        dublinBikesDocks: List<DublinBikesDock>,
        dublinBusStops: List<DublinBusStop>,
        luasStops: List<LuasStop>,
        swordsExpressStops: List<SwordsExpressStop>
    ): Response {
        val serviceLocations = mutableListOf<ServiceLocation>()
        serviceLocations.addAll(aircoachStops)
        serviceLocations.addAll(busEireannStops)
        serviceLocations.addAll(dartStations)
        serviceLocations.addAll(dublinBikesDocks)
        serviceLocations.addAll(dublinBusStops)
        serviceLocations.addAll(luasStops)
        serviceLocations.addAll(swordsExpressStops)
//        val sorted = serviceLocations.associateTo(TreeMap()) { serviceLocation ->
//            LocationUtils.haversineDistance(coordinate, serviceLocation.coordinate) to serviceLocation }
        val sorted = TreeMap<Double, ServiceLocation>()
        for (serviceLocation in serviceLocations) {
            sorted[LocationUtils.haversineDistance(coordinate, serviceLocation.coordinate)] = serviceLocation
        }
        val result = mutableListOf<ServiceLocation>()
        result.addAll(findFirstN(3, Operator.rail(), sorted))
        result.addAll(findFirstN(7, Operator.bike(), sorted))
        result.addAll(findFirstN(15, Operator.bus(), sorted))
        result.addAll(findFirstN(5, Operator.tram(), sorted))
        val isComplete = aircoachStops.isNotEmpty() && dartStations.isNotEmpty() && dublinBikesDocks.isNotEmpty() &&
                dublinBusStops.isNotEmpty() && luasStops.isNotEmpty() && swordsExpressStops.isNotEmpty()
        return Response(result, isComplete)
    }

    private fun findFirstN(
        limit: Int,
        operators: EnumSet<Operator>,
        serviceLocations: TreeMap<Double, ServiceLocation>
    ): List<ServiceLocation> {
        var count = 0
        val result = mutableListOf<ServiceLocation>()
        for ((_, value) in serviceLocations) {
            if (count >= limit) {
                break
            }
            if (operators.intersectWith(value.operators)) {
                result.add(value)
                count++
            }
        }
        return result
    }

}

data class Response(
    val serviceLocations: List<ServiceLocation>,
    val isComplete: Boolean
)

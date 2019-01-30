package ie.dublinmapper.domain.usecase

import ie.dublinmapper.domain.model.*
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.util.*
import io.reactivex.Observable
import io.reactivex.functions.Function4
import java.util.*
import javax.inject.Inject

class NearbyUseCase @Inject constructor(
    private val dartStationRepository: Repository<DartStation>,
    private val dublinBikesDockRepository: Repository<DublinBikesDock>,
    private val dublinBusStopRepository: Repository<DublinBusStop>,
    private val luasStopRepository: Repository<LuasStop>,
    private val thread: Thread
) {

    fun getNearbyServiceLocations(coordinate: Coordinate): Observable<Response> {
        return Observable.combineLatest(
            dartStationRepository.getAll().startWith(emptyList<DartStation>()).subscribeOn(thread.io),
            dublinBikesDockRepository.getAll().startWith(emptyList<DublinBikesDock>()).subscribeOn(thread.io),
            dublinBusStopRepository.getAll().startWith(emptyList<DublinBusStop>()).subscribeOn(thread.io),
            luasStopRepository.getAll().startWith(emptyList<LuasStop>()).subscribeOn(thread.io),
            Function4 { dartStations, dublinBikesDocks, dublinBusStops, luasStops ->
                filter(coordinate, dartStations, dublinBikesDocks, dublinBusStops, luasStops) }
        )
    }

    private fun filter(
        coordinate: Coordinate,
        dartStations: List<DartStation>,
        dublinBikesDocks: List<DublinBikesDock>,
        dublinBusStops: List<DublinBusStop>,
        luasStops: List<LuasStop>
    ): Response {
        val serviceLocations = mutableListOf<ServiceLocation>()
        serviceLocations.addAll(dartStations)
        serviceLocations.addAll(dublinBikesDocks)
        serviceLocations.addAll(dublinBusStops)
        serviceLocations.addAll(luasStops)
//        val sorted = serviceLocations.associateTo(TreeMap()) { serviceLocation ->
//            LocationUtils.haversineDistance(coordinate, serviceLocation.coordinate) to serviceLocation }
        val sorted = TreeMap<Double, ServiceLocation>()
        for (serviceLocation in serviceLocations) {
            sorted[LocationUtils.haversineDistance(coordinate, serviceLocation.coordinate)] = serviceLocation
        }
        val result = mutableListOf<ServiceLocation>()
        result.addAll(findFirstN(3, Operator.rail(), sorted))
        result.addAll(findFirstN(7, Operator.bike(), sorted))
        result.addAll(findFirstN(12, Operator.bus(), sorted))
        result.addAll(findFirstN(5, Operator.tram(), sorted))
        val isComplete = dartStations.isNotEmpty() && dublinBikesDocks.isNotEmpty() &&
                dublinBusStops.isNotEmpty() && luasStops.isNotEmpty()
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

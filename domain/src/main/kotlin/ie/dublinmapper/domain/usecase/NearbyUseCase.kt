package ie.dublinmapper.domain.usecase

import ie.dublinmapper.domain.model.ServiceLocation
import ie.dublinmapper.domain.model.dart.DartStation
import ie.dublinmapper.domain.model.dublinbikes.DublinBikesDock
import ie.dublinmapper.domain.model.dublinbus.DublinBusStop
import ie.dublinmapper.domain.model.luas.LuasStop
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.LocationUtils
import ie.dublinmapper.util.Operator
import io.reactivex.Observable
import io.reactivex.functions.Function4
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class NearbyUseCase @Inject constructor(
    private val dartStationRepository: Repository<DartStation>,
    private val dublinBikesDockRepository: Repository<DublinBikesDock>,
    private val dublinBusStopRepository: Repository<DublinBusStop>,
    private val luasStopRepository: Repository<LuasStop>
) {

    fun getNearbyServiceLocations(coordinate: Coordinate): Observable<Collection<ServiceLocation>> {
        return Observable.combineLatest(
            dartStationRepository.getAll().startWith(emptyList<DartStation>()).subscribeOn(Schedulers.io()),
            dublinBikesDockRepository.getAll().startWith(emptyList<DublinBikesDock>()).subscribeOn(Schedulers.io()),
            dublinBusStopRepository.getAll().startWith(emptyList<DublinBusStop>()).subscribeOn(Schedulers.io()),
            luasStopRepository.getAll().startWith(emptyList<LuasStop>()).subscribeOn(Schedulers.io()),
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
    ): Collection<ServiceLocation> {
        val serviceLocations = mutableListOf<ServiceLocation>()
        serviceLocations.addAll(dartStations)
        serviceLocations.addAll(dublinBikesDocks)
        serviceLocations.addAll(dublinBusStops)
        serviceLocations.addAll(luasStops)
        val sorted = TreeMap<Double, ServiceLocation>()
        for (serviceLocation in serviceLocations) {
            sorted[LocationUtils.haversineDistance(coordinate, serviceLocation.coordinate)] = serviceLocation
        }
        val result = mutableListOf<ServiceLocation>()
        result.addAll(findFirstN(3, Operator.DART, sorted))
        result.addAll(findFirstN(7, Operator.DUBLIN_BIKES, sorted))
        result.addAll(findFirstN(12, Operator.DUBLIN_BUS, sorted))
        result.addAll(findFirstN(5, Operator.LUAS, sorted))
        return result
    }

    private fun findFirstN(
        limit: Int,
        operator: Operator,
        serviceLocations: TreeMap<Double, ServiceLocation>
    ): Collection<ServiceLocation> {
        var count = 0
        val result = mutableListOf<ServiceLocation>()
        for ((_, value) in serviceLocations) {
            if (count >= limit) {
                break
            }
            if (value.operator == operator) {
                result.add(value)
                count++
            }
        }
        return result
    }

}

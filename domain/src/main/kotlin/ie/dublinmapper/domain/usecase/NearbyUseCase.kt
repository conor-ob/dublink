package ie.dublinmapper.domain.usecase

import ie.dublinmapper.domain.model.ServiceLocation
import ie.dublinmapper.domain.model.dart.DartStation
import ie.dublinmapper.domain.model.dublinbikes.DublinBikesDock
import ie.dublinmapper.domain.model.dublinbus.DublinBusStop
import ie.dublinmapper.domain.model.luas.LuasStop
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.util.CollectionUtils
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.LocationUtils
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

    fun getNearbyServiceLocations(coordinate: Coordinate): Observable<SortedMap<Double, ServiceLocation>> {
        return Observable.combineLatest(
            dartStationRepository.getAll().subscribeOn(Schedulers.io()),
            dublinBikesDockRepository.getAll().subscribeOn(Schedulers.io()),
            dublinBusStopRepository.getAll().subscribeOn(Schedulers.io()),
            luasStopRepository.getAll().subscribeOn(Schedulers.io()),
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
    ): SortedMap<Double, ServiceLocation> {
        val serviceLocations = mutableListOf<ServiceLocation>()
        serviceLocations.addAll(dartStations)
//        serviceLocations.addAll(dublinBikesDocks)
//        serviceLocations.addAll(dublinBusStops)
//        serviceLocations.addAll(luasStops)
        val sorted = TreeMap<Double, ServiceLocation>()
        for (serviceLocation in serviceLocations) {
            sorted[LocationUtils.haversineDistance(coordinate, serviceLocation.coordinate)] = serviceLocation
        }
        return CollectionUtils.headMap(sorted, 5)
    }

}

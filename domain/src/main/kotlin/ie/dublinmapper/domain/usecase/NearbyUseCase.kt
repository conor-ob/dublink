package ie.dublinmapper.domain.usecase

import ie.dublinmapper.domain.model.ServiceLocation
import ie.dublinmapper.domain.model.dublinbikes.DublinBikesDock
import ie.dublinmapper.domain.model.luas.LuasStop
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.util.CollectionUtils
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.LocationUtils
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class NearbyUseCase @Inject constructor(
    private val dublinBikesDockRepository: Repository<DublinBikesDock>,
    private val luasStopRepository: Repository<LuasStop>
) {

    fun getNearbyServiceLocations(coordinate: Coordinate): Observable<SortedMap<Double, ServiceLocation>> {
        return Observable.combineLatest(
            dublinBikesDockRepository.getAll().subscribeOn(Schedulers.io()),
            luasStopRepository.getAll().subscribeOn(Schedulers.io()),
            BiFunction { dublinBikesDocks, luasStops -> filter(coordinate, dublinBikesDocks, luasStops) }
        )
    }

    private fun filter(coordinate: Coordinate, dublinBikesDocks: List<DublinBikesDock>, luasStops: List<LuasStop>): SortedMap<Double, ServiceLocation> {
        val serviceLocations = mutableListOf<ServiceLocation>()
        serviceLocations.addAll(dublinBikesDocks)
        serviceLocations.addAll(luasStops)
        val sorted = TreeMap<Double, ServiceLocation>()
        for (serviceLocation in serviceLocations) {
            sorted[LocationUtils.haversineDistance(coordinate, serviceLocation.coordinate)] = serviceLocation
        }
        return CollectionUtils.headMap(sorted, 30)
    }

}

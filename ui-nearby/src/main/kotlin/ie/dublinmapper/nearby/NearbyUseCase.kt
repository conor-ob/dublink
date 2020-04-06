package ie.dublinmapper.nearby

import ie.dublinmapper.domain.repository.AggregatedServiceLocationRepository
import ie.dublinmapper.domain.repository.ServiceLocationRepository
import ie.dublinmapper.domain.service.LocationProvider
import ie.dublinmapper.domain.service.RxScheduler
import ie.dublinmapper.domain.util.haversine
import ie.dublinmapper.domain.util.truncateHead
import io.reactivex.Observable
import io.rtpi.api.Coordinate
import io.rtpi.api.ServiceLocation
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

class NearbyUseCase @Inject constructor(
    private val serviceLocationRepository: AggregatedServiceLocationRepository,
    private val scheduler: RxScheduler,
    private val locationProvider: LocationProvider
) {

    fun getNearbyServiceLocations(): Observable<NearbyResponse> {
//        return Observable.concat(
//            locationProvider.getLastKnownLocation(),
//            locationProvider.getLocationUpdates()
//        )
//            .throttleFirst(30, TimeUnit.SECONDS)
//            .flatMap { filterNearby(it) }
        TODO()
    }

    private fun filterNearby(coordinate: Coordinate): Observable<NearbyResponse> {
        TODO()
//        return serviceLocationRepository.get().map { filterBlah(coordinate, it) }
    }

    private fun filterBlah(
        coordinate: Coordinate,
        serviceLocations: List<ServiceLocation>
    ): NearbyResponse {
        return NearbyResponse(
            serviceLocations
                .associateBy { coordinate.haversine(it.coordinate) }
                .toSortedMap()
                .truncateHead(50)
        )
    }
}

data class NearbyResponse(
    val serviceLocations: SortedMap<Double, ServiceLocation>
)

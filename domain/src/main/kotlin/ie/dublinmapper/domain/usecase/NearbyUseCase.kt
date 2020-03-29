package ie.dublinmapper.domain.usecase

import ie.dublinmapper.domain.repository.LocationRepository
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
    @Named("SERVICE_LOCATION") private val locationRepository: LocationRepository,
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
        return locationRepository.get().map { filterBlah(coordinate, it) }
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

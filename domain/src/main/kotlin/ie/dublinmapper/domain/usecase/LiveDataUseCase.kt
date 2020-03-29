package ie.dublinmapper.domain.usecase

import ie.dublinmapper.domain.repository.LiveDataKey
import ie.dublinmapper.domain.repository.LiveDataRepository
import ie.dublinmapper.domain.repository.LocationRepository
import ie.dublinmapper.domain.repository.ServiceLocationKey
import io.reactivex.Observable
import io.rtpi.api.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

class LiveDataUseCase @Inject constructor(
    @Named("SERVICE_LOCATION") private val locationRepository: LocationRepository,
    @Named("LIVE_DATA") private val liveDataRepository: LiveDataRepository
) {

    fun getServiceLocation(serviceLocationId: String, service: Service): Observable<ServiceLocation> {
        return locationRepository.get(ServiceLocationKey(service = service, locationId = serviceLocationId))
    }

    fun getLiveDataStream(serviceLocationId: String, serviceLocationName: String, service: Service): Observable<LiveDataResponse> {
        return Observable.interval(0L, 65L, TimeUnit.SECONDS)
            .flatMap {
                getLiveData(serviceLocationId, service).map {
                    LiveDataResponse(service, serviceLocationName, it, State.COMPLETE)
                }
            }
    }

    private fun getLiveData(serviceLocationId: String, service: Service): Observable<List<LiveData>> {
        return liveDataRepository.get(LiveDataKey(service = service, locationId = serviceLocationId))
    }
}

data class LiveDataResponse(
    val service: Service,
    val serviceLocationName: String,
    val liveData: List<LiveData>,
    val state: State
)

enum class State {
    LOADING, COMPLETE
}

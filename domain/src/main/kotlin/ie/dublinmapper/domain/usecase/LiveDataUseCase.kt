package ie.dublinmapper.domain.usecase

import ie.dublinmapper.domain.repository.LiveDataKey
import ie.dublinmapper.domain.repository.LiveDataRepository
import ie.dublinmapper.domain.repository.LocationRepository
import ie.dublinmapper.domain.repository.ServiceLocationKey
import ie.dublinmapper.domain.service.PreferenceStore
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.rtpi.api.*
import io.rtpi.util.LiveDataGrouper
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

class LiveDataUseCase @Inject constructor(
    @Named("SERVICE_LOCATION") private val locationRepository: LocationRepository,
    @Named("LIVE_DATA") private val liveDataRepository: LiveDataRepository,
    private val preferenceStore: PreferenceStore
) {

    fun getServiceLocation(serviceLocationId: String, service: Service): Observable<ServiceLocation> {
        return locationRepository.get(ServiceLocationKey(service = service, locationId = serviceLocationId))
    }

    fun getLiveDataStream(serviceLocation: ServiceLocation): Observable<LiveDataResponse> {
        return Observable.interval(0L, preferenceStore.getLiveDataRefreshInterval(), TimeUnit.SECONDS)
            .flatMap { getLiveData(serviceLocation) }
    }

    fun getGroupedLiveDataStream(serviceLocation: ServiceLocation): Observable<GroupedLiveDataResponse> {
        return Observable.interval(0L, preferenceStore.getLiveDataRefreshInterval(), TimeUnit.SECONDS)
            .flatMap { getGroupedLiveData(serviceLocation) }
    }

    private fun getGroupedLiveData(serviceLocation: ServiceLocation): Observable<GroupedLiveDataResponse> {
        return getLiveData(serviceLocation).map { liveDataResponse ->
            GroupedLiveDataResponse(
                serviceLocation = liveDataResponse.serviceLocation,
                liveData = LiveDataGrouper.groupLiveData(liveDataResponse.liveData),
                state = liveDataResponse.state
            )
        }
    }

    private fun getLiveData(serviceLocation: ServiceLocation): Observable<LiveDataResponse> {
        return liveDataRepository.get(
            LiveDataKey(
                service = serviceLocation.service,
                locationId = serviceLocation.id
            )
        ).map { liveData ->
            LiveDataResponse(
                serviceLocation = serviceLocation,
                liveData = liveData,
                state = State.COMPLETE
            )
        }.onErrorReturn { e ->
            LiveDataResponse(
                serviceLocation = serviceLocation,
                liveData = emptyList(),
                state = State.ERROR
            )
        }
    }
}

data class LiveDataResponse(
    val serviceLocation: ServiceLocation,
    val liveData: List<LiveData>,
    val state: State
)

data class GroupedLiveDataResponse(
    val serviceLocation: ServiceLocation,
    val liveData: List<List<LiveData>>,
    val state: State
)

enum class State {
    LOADING,
    SKIPPED,
    COMPLETE,
    ERROR
}

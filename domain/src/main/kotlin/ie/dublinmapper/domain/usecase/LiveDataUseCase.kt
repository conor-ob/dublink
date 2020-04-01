package ie.dublinmapper.domain.usecase

import ie.dublinmapper.domain.repository.LiveDataKey
import ie.dublinmapper.domain.repository.LiveDataRepository
import ie.dublinmapper.domain.repository.LocationRepository
import ie.dublinmapper.domain.repository.ServiceLocationKey
import ie.dublinmapper.domain.service.PreferenceStore
import io.reactivex.Observable
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

    fun getGroupedLiveDataStream(serviceLocation: ServiceLocation): Observable<LiveDataResponse> {
        return Observable.interval(0L, preferenceStore.getLiveDataRefreshInterval(), TimeUnit.SECONDS)
            .flatMap { getGroupedLiveData(serviceLocation) }
    }

    private fun getGroupedLiveData(serviceLocation: ServiceLocation): Observable<LiveDataResponse> {
        return getLiveData(serviceLocation).map { liveDataResponse ->
            if (liveDataResponse is LiveDataResponse.Complete) {
                LiveDataResponse.Grouped(
                    serviceLocation,
                    LiveDataGrouper.groupLiveData(liveDataResponse.liveData)
                )
            } else {
                liveDataResponse
            }
        }
    }

    private fun getLiveData(serviceLocation: ServiceLocation): Observable<LiveDataResponse> {
        return liveDataRepository.get(
            LiveDataKey(
                service = serviceLocation.service,
                locationId = serviceLocation.id
            )
        ).map<LiveDataResponse> { liveData ->
            LiveDataResponse.Complete(
                serviceLocation = serviceLocation,
                liveData = liveData
            )
        }.onErrorReturn { throwable ->
            LiveDataResponse.Error(
                serviceLocation = serviceLocation,
                throwable = throwable
            )
        }
    }
}

sealed class LiveDataResponse(
    open val serviceLocation: ServiceLocation
) {

    data class Loading(
        override val serviceLocation: ServiceLocation
    ) : LiveDataResponse(serviceLocation)

    data class Skipped(
        override val serviceLocation: ServiceLocation
    ) : LiveDataResponse(serviceLocation)

    data class Complete(
        override val serviceLocation: ServiceLocation,
        val liveData: List<LiveData>
    ) : LiveDataResponse(serviceLocation)

    data class Grouped(
        override val serviceLocation: ServiceLocation,
        val liveData: List<List<LiveData>>
    ) : LiveDataResponse(serviceLocation)

    data class Error(
        override val serviceLocation: ServiceLocation,
        val throwable: Throwable
    ) : LiveDataResponse(serviceLocation)
}

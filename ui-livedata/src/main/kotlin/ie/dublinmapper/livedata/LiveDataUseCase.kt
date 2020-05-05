package ie.dublinmapper.livedata

import ie.dublinmapper.domain.model.DubLinkServiceLocation
import ie.dublinmapper.domain.repository.AggregatedServiceLocationRepository
import ie.dublinmapper.domain.repository.FavouriteRepository
import ie.dublinmapper.domain.repository.LiveDataKey
import ie.dublinmapper.domain.repository.LiveDataRepository
import ie.dublinmapper.domain.repository.LiveDataResponse
import ie.dublinmapper.domain.repository.ServiceLocationKey
import ie.dublinmapper.domain.repository.ServiceLocationResponse
import io.reactivex.Observable
import io.rtpi.api.LiveData
import io.rtpi.api.Service
import javax.inject.Inject

class LiveDataUseCase @Inject constructor(
    private val serviceLocationRepository: AggregatedServiceLocationRepository,
    private val liveDataRepository: LiveDataRepository,
    private val favouriteRepository: FavouriteRepository
) {

    fun getServiceLocation(
        service: Service,
        locationId: String
    ): Observable<ServiceLocationPresentationResponse> {
        return serviceLocationRepository.get(
            ServiceLocationKey(
                service = service,
                locationId = locationId
            )
        ).map { response ->
            when (response) {
                is ServiceLocationResponse.Data -> ServiceLocationPresentationResponse.Data(
                    serviceLocation = response.serviceLocations.first()
                )
                is ServiceLocationResponse.Error -> ServiceLocationPresentationResponse.Error(
                    throwable = response.throwable
                )
            }
        }
    }

    fun getLiveData(
        service: Service,
        locationId: String,
        refresh: Boolean
    ): Observable<LiveDataPresentationResponse> {
        return liveDataRepository.get(
            LiveDataKey(
                service = service,
                locationId = locationId
            ),
            refresh = refresh
        ).map { response ->
            when (response) {
                is LiveDataResponse.Data -> LiveDataPresentationResponse.Data(
                    liveData = response.liveData
                )
                is LiveDataResponse.Error -> LiveDataPresentationResponse.Error(
                    throwable = response.throwable
                )
            }
        }.onErrorReturn { throwable ->
            LiveDataPresentationResponse.Error(throwable = throwable)
        }
    }

    fun saveFavourite(serviceLocation: DubLinkServiceLocation): Observable<ServiceLocationPresentationResponse> {
        clearServiceLocationCache(serviceLocation.service)
        favouriteRepository.saveFavourite(serviceLocation)
        return getServiceLocation(serviceLocation.service, serviceLocation.id)
    }

    fun removeFavourite(service: Service, serviceLocationId: String): Observable<ServiceLocationPresentationResponse> {
        clearServiceLocationCache(service)
        favouriteRepository.removeFavourite(serviceLocationId, service)
        return getServiceLocation(service, serviceLocationId)
    }

    private fun clearServiceLocationCache(service: Service) {
        serviceLocationRepository.clearCache(service)
    }
}

sealed class ServiceLocationPresentationResponse {

    data class Data(val serviceLocation: DubLinkServiceLocation) : ServiceLocationPresentationResponse()

    data class Error(val throwable: Throwable) : ServiceLocationPresentationResponse()
}

sealed class LiveDataPresentationResponse {

    data class Data(val liveData: List<LiveData>) : LiveDataPresentationResponse()

    data class Error(val throwable: Throwable) : LiveDataPresentationResponse()
}

package io.dublink.repository.location

import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.repository.ServiceLocationKey
import io.dublink.domain.repository.ServiceLocationRepository
import io.dublink.domain.repository.ServiceLocationResponse
import io.reactivex.Observable
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation

class DefaultServiceLocationRepository(
    private val service: Service,
    private val serviceLocationStore: StoreRoom<List<DubLinkServiceLocation>, Service>
) : ServiceLocationRepository {

    override fun get(): Observable<ServiceLocationResponse> {
        return serviceLocationStore
            .get(service)
            .map<ServiceLocationResponse> { serviceLocations ->
                ServiceLocationResponse.Data(
                    service = service,
                    serviceLocations = serviceLocations
                )
            }
            .onErrorReturn { throwable ->
                ServiceLocationResponse.Error(
                    service = service,
                    throwable = throwable
                )
            }
    }

    override fun getFavourites(): Observable<ServiceLocationResponse> {
        return get()
            .map { response ->
                when (response) {
                    is ServiceLocationResponse.Data -> response.copy(
                        serviceLocations = response.serviceLocations.filter { it.isFavourite }
                    )
                    is ServiceLocationResponse.Error -> response
                }
            }
    }

    override fun get(key: ServiceLocationKey): Observable<ServiceLocationResponse> {
        return get()
            .map { response ->
                when (response) {
                    is ServiceLocationResponse.Data -> {
                        val serviceLocation = response.serviceLocations.find { it.id == key.locationId }
                        if (serviceLocation != null) {
                            ServiceLocationResponse.Data(
                                service = service,
                                serviceLocations = listOf(serviceLocation)
                            )
                        } else {
                            ServiceLocationResponse.Error(
                                service = service,
                                throwable = IllegalStateException(
                                    ServiceLocation::class.java.simpleName +
                                        " not found for ${key.service}" +
                                        " with location ID ${key.locationId}"
                                )
                            )
                        }
                    }
                    is ServiceLocationResponse.Error -> response
                }
            }
    }

    override fun clearCache(service: Service) {
        serviceLocationStore.clear()
    }
}

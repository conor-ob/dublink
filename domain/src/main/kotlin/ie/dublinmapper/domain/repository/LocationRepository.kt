package ie.dublinmapper.domain.repository

import io.reactivex.Observable
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation

interface LocationRepository {
    fun get(): Observable<List<ServiceLocation>>
    fun getFavourites(): Observable<List<ServiceLocation>>
    fun get(key: ServiceLocationKey): Observable<ServiceLocation>
    fun clearCache(service: Service)
}

data class ServiceLocationKey(
    val service: Service,
    val locationId: String
)

data class ServiceLocationResponse(
    val service: Service,
    val serviceLocations: List<ServiceLocation>,
    val isSuccessful: Boolean,
    val errorMessage: String? = null,
    val exception: Exception? = null
)

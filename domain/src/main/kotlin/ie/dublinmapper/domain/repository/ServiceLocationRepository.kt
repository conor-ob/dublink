package ie.dublinmapper.domain.repository

import io.reactivex.Observable
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation

interface ServiceLocationRepository {
    fun get(): Observable<ServiceLocationResponse>
    fun getFavourites(): Observable<ServiceLocationResponse>
    fun get(key: ServiceLocationKey): Observable<ServiceLocationResponse>
    fun clearCache(service: Service)
}

interface AggregatedServiceLocationRepository {
    fun get(): Observable<List<ServiceLocationResponse>>
    fun getFavourites(): Observable<List<ServiceLocationResponse>>
    fun get(key: ServiceLocationKey): Observable<ServiceLocationResponse>
    fun clearCache(service: Service)
}

data class ServiceLocationKey(
    val service: Service,
    val locationId: String
)

sealed class ServiceLocationResponse {

    abstract val service: Service

    data class Data(
        override val service: Service,
        val serviceLocations: List<ServiceLocation>
    ) : ServiceLocationResponse()

    data class Error(
        override val service: Service,
        val throwable: Throwable
    ) : ServiceLocationResponse()
}

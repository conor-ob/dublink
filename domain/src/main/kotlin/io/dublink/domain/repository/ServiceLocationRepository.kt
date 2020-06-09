package io.dublink.domain.repository

import io.dublink.domain.model.DubLinkServiceLocation
import io.reactivex.Observable
import io.rtpi.api.Coordinate
import io.rtpi.api.Service

interface ServiceLocationRepository {
    fun get(refresh: Boolean): Observable<ServiceLocationResponse>
    fun getNearest(coordinate: Coordinate, limit: Int): Observable<ServiceLocationResponse>
    fun getFavourites(): Observable<ServiceLocationResponse>
    fun get(key: ServiceLocationKey): Observable<ServiceLocationResponse>
    fun clearCache(service: Service)
}

interface AggregatedServiceLocationRepository {
    fun get(refresh: Boolean): Observable<AggregatedServiceLocationResponse>
    fun getFavourites(): Observable<AggregatedServiceLocationResponse>
    fun stream(): Observable<AggregatedServiceLocationResponse>
    fun streamNearest(coordinate: Coordinate, limit: Int): Observable<AggregatedServiceLocationResponse>
    fun get(key: ServiceLocationKey): Observable<ServiceLocationResponse>
    fun clearCache(service: Service)
    fun clearAllCaches()
}

data class ServiceLocationKey(
    val service: Service,
    val locationId: String
)

sealed class ServiceLocationResponse {

    abstract val service: Service

    data class Data(
        override val service: Service,
        val serviceLocations: List<DubLinkServiceLocation>
    ) : ServiceLocationResponse()

    data class Error(
        override val service: Service,
        val throwable: Throwable
    ) : ServiceLocationResponse()
}

data class AggregatedServiceLocationResponse(
    val serviceLocationResponses: List<ServiceLocationResponse>
) {

    val dataResponses = serviceLocationResponses.filterIsInstance<ServiceLocationResponse.Data>()
    val errorResponses = serviceLocationResponses.filterIsInstance<ServiceLocationResponse.Error>()
    val serviceLocations = dataResponses.flatMap { response -> response.serviceLocations }
}

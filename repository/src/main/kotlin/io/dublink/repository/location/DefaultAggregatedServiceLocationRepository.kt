package io.dublink.repository.location

import io.dublink.domain.repository.AggregatedServiceLocationRepository
import io.dublink.domain.repository.AggregatedServiceLocationResponse
import io.dublink.domain.repository.ServiceLocationKey
import io.dublink.domain.repository.ServiceLocationRepository
import io.dublink.domain.repository.ServiceLocationResponse
import io.dublink.domain.service.EnabledServiceManager
import io.reactivex.Observable
import io.rtpi.api.Coordinate
import io.rtpi.api.Service

class DefaultAggregatedServiceLocationRepository(
    private val serviceLocationRepositories: Map<Service, ServiceLocationRepository>,
    private val enabledServiceManager: EnabledServiceManager
) : AggregatedServiceLocationRepository {

    override fun get(): Observable<AggregatedServiceLocationResponse> {
        return Observable.zip(
            enabledServiceManager.getEnabledServices().map { enabledService ->
                serviceLocationRepositories.getValue(enabledService).get()
            }
        ) { serviceLocationStreams -> aggregate(serviceLocationStreams) }
    }

    override fun getFavourites(): Observable<AggregatedServiceLocationResponse> {
        return Observable.zip(
            enabledServiceManager.getEnabledServices().map { enabledService ->
                serviceLocationRepositories.getValue(enabledService).getFavourites()
            }
        ) { serviceLocationStreams -> aggregate(serviceLocationStreams) }
    }

    override fun getNearest(coordinate: Coordinate, limit: Int): Observable<AggregatedServiceLocationResponse> {
        return Observable.zip(
            enabledServiceManager.getEnabledServices().map { enabledService ->
                serviceLocationRepositories.getValue(enabledService).getNearest(coordinate, limit)
                    .startWith(ServiceLocationResponse.Data(enabledService, emptyList()))
            }
        ) { serviceLocationStreams -> aggregate(serviceLocationStreams) }
    }

    override fun stream(): Observable<AggregatedServiceLocationResponse> {
        return Observable.combineLatest(
            enabledServiceManager.getEnabledServices().map { enabledService ->
                serviceLocationRepositories.getValue(enabledService).get()
                    .startWith(ServiceLocationResponse.Data(enabledService, emptyList()))
            }
        ) { serviceLocationStreams -> aggregate(serviceLocationStreams) }
    }

    override fun streamNearest(coordinate: Coordinate, limit: Int): Observable<AggregatedServiceLocationResponse> {
        return Observable.combineLatest(
            enabledServiceManager.getEnabledServices().map { enabledService ->
                serviceLocationRepositories.getValue(enabledService).getNearest(coordinate, limit)
                    .startWith(ServiceLocationResponse.Data(enabledService, emptyList()))
            }
        ) { serviceLocationStreams -> aggregate(serviceLocationStreams) }
    }

    override fun get(key: ServiceLocationKey): Observable<ServiceLocationResponse> {
        return serviceLocationRepositories.getValue(key.service).get(key)
    }

    override fun clearCache(service: Service) {
        serviceLocationRepositories.getValue(service).clearCache(service)
    }

    override fun clearAllCaches() {
        serviceLocationRepositories.forEach { it.value.clearCache(it.key) }
    }

    private fun aggregate(serviceLocationStreams: Array<out Any>): AggregatedServiceLocationResponse {
        return AggregatedServiceLocationResponse(
            serviceLocationResponses = serviceLocationStreams.map { it as ServiceLocationResponse }
        )
    }
}

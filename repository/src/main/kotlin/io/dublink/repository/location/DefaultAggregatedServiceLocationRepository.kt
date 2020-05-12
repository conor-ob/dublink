package io.dublink.repository.location

import io.dublink.domain.repository.AggregatedServiceLocationRepository
import io.dublink.domain.repository.AggregatedServiceLocationResponse
import io.dublink.domain.repository.ServiceLocationKey
import io.dublink.domain.repository.ServiceLocationRepository
import io.dublink.domain.repository.ServiceLocationResponse
import io.dublink.domain.service.EnabledServiceManager
import io.reactivex.Observable
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
        ) { serviceLocationStreams ->
            AggregatedServiceLocationResponse(
                serviceLocationResponses = serviceLocationStreams.map { it as ServiceLocationResponse }
            )
        }
    }

    override fun stream(): Observable<AggregatedServiceLocationResponse> {
        return Observable.combineLatest(
            enabledServiceManager.getEnabledServices().map { enabledService ->
                serviceLocationRepositories.getValue(enabledService).get()
                    .startWith(ServiceLocationResponse.Data(enabledService, emptyList()))
            }
        ) { serviceLocationStreams ->
            AggregatedServiceLocationResponse(
                serviceLocationResponses = serviceLocationStreams.map { it as ServiceLocationResponse }
            )
        }
    }

    override fun getFavourites(): Observable<AggregatedServiceLocationResponse> {
        return Observable.zip(
            enabledServiceManager.getEnabledServices().map { enabledService ->
                serviceLocationRepositories.getValue(enabledService).getFavourites()
            }
        ) { serviceLocationStreams ->
            AggregatedServiceLocationResponse(
                serviceLocationResponses = serviceLocationStreams.map { it as ServiceLocationResponse }
            )
        }
    }

    override fun streamFavourites(): Observable<AggregatedServiceLocationResponse> {
        return Observable.combineLatest(
            enabledServiceManager.getEnabledServices().map { enabledService ->
                serviceLocationRepositories.getValue(enabledService).getFavourites()
                    .startWith(ServiceLocationResponse.Data(enabledService, emptyList()))
            }
        ) { serviceLocationStreams ->
            AggregatedServiceLocationResponse(
                serviceLocationResponses = serviceLocationStreams.map { it as ServiceLocationResponse }
            )
        }
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
}

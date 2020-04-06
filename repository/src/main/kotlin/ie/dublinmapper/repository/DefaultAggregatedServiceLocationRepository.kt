package ie.dublinmapper.repository

import ie.dublinmapper.domain.repository.AggregatedServiceLocationRepository
import ie.dublinmapper.domain.repository.ServiceLocationRepository
import ie.dublinmapper.domain.repository.ServiceLocationResponse
import ie.dublinmapper.domain.repository.ServiceLocationKey
import ie.dublinmapper.domain.service.EnabledServiceManager
import io.reactivex.Observable
import io.rtpi.api.Service

class DefaultAggregatedServiceLocationRepository(
    private val serviceLocationRepositories: Map<Service, ServiceLocationRepository>,
    private val enabledServiceManager: EnabledServiceManager
) : AggregatedServiceLocationRepository {

    override fun get(): Observable<List<ServiceLocationResponse>> {
        return Observable.combineLatest(
            enabledServiceManager.getEnabledServices().map { enabledService ->
                serviceLocationRepositories.getValue(enabledService).get()
            }
        ) { serviceLocationStreams -> serviceLocationStreams.map { it as ServiceLocationResponse } }
    }

    override fun getFavourites(): Observable<List<ServiceLocationResponse>> {
        return Observable.combineLatest(
            enabledServiceManager.getEnabledServices().map { enabledService ->
                serviceLocationRepositories.getValue(enabledService).getFavourites()
            }
        ) { serviceLocationStreams -> serviceLocationStreams.map { it as ServiceLocationResponse } }
    }

    override fun get(key: ServiceLocationKey): Observable<ServiceLocationResponse> {
        return serviceLocationRepositories.getValue(key.service).get(key)
    }

    override fun clearCache(service: Service) {
        return serviceLocationRepositories.getValue(service).clearCache(service)
    }
}

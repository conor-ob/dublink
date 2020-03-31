package ie.dublinmapper.repository

import ie.dublinmapper.domain.repository.LocationRepository
import ie.dublinmapper.domain.repository.ServiceLocationKey
import ie.dublinmapper.domain.service.EnabledServiceManager
import io.reactivex.Observable
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation

class AggregatedLocationRepository(
    private val locationRepositories: Map<Service, LocationRepository>,
    private val enabledServiceManager: EnabledServiceManager
) : LocationRepository {

    override fun get(): Observable<List<ServiceLocation>> {
        return Observable.combineLatest(
            enabledServiceManager.getEnabledServices().map { enabledService ->
                locationRepositories.getValue(enabledService).get()
//                    .startWith(emptyList<ServiceLocation>())
            }
        ) { serviceLocationStreams ->
            serviceLocationStreams.flatMap { stream ->
                (stream as List<*>).map { it as ServiceLocation }
            }
        }
    }

    override fun getFavourites(): Observable<List<ServiceLocation>> {
        return Observable.combineLatest(
            enabledServiceManager.getEnabledServices().map { enabledService ->
                locationRepositories.getValue(enabledService).getFavourites()
//                    .startWith(emptyList<ServiceLocation>())
            }
        ) { serviceLocationStreams ->
            serviceLocationStreams.flatMap { stream ->
                (stream as List<*>).map { it as ServiceLocation }
            }
        }
    }

    override fun get(key: ServiceLocationKey): Observable<ServiceLocation> {
        return locationRepositories.getValue(key.service).get(key)
    }

    override fun clearCache(service: Service) {
        return locationRepositories.getValue(service).clearCache(service)
    }
}

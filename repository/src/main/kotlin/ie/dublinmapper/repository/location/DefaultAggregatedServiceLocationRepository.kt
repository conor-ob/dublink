package ie.dublinmapper.repository.location

import ie.dublinmapper.domain.repository.AggregatedServiceLocationRepository
import ie.dublinmapper.domain.repository.ServiceLocationKey
import ie.dublinmapper.domain.repository.ServiceLocationRepository
import ie.dublinmapper.domain.repository.ServiceLocationResponse
import ie.dublinmapper.domain.service.EnabledServiceManager
import io.reactivex.Observable
import io.reactivex.functions.Function6
import io.rtpi.api.Service

class DefaultAggregatedServiceLocationRepository(
    private val serviceLocationRepositories: Map<Service, ServiceLocationRepository>,
    private val enabledServiceManager: EnabledServiceManager
) : AggregatedServiceLocationRepository {

    override fun get(): Observable<List<ServiceLocationResponse>> {
        return Observable.combineLatest(
            get(Service.AIRCOACH),
            get(Service.BUS_EIREANN),
            get(Service.DUBLIN_BIKES),
            get(Service.DUBLIN_BUS),
            get(Service.IRISH_RAIL),
            get(Service.LUAS),
            Function6 { t1, t2, t3, t4, t5, t6 ->
                listOf(t1, t2, t3, t4, t5, t6)
            }
        )
//        return Observable.combineLatest(
//            enabledServiceManager.getEnabledServices().map { enabledService ->
//                serviceLocationRepositories.getValue(enabledService).get()
//                    .startWith(emptyList())
//                    .subscribeOn(Schedulers.newThread())
//            }
//        ) { serviceLocationStreams -> serviceLocationStreams.map { it as ServiceLocationResponse } }
    }

    private fun get(service: Service): Observable<ServiceLocationResponse> =
        if (enabledServiceManager.isServiceEnabled(service)) {
            serviceLocationRepositories.getValue(service).get()
                .startWith(ServiceLocationResponse.Data(service, emptyList()))
        } else {
            Observable.just(ServiceLocationResponse.Data(service, emptyList()))
        }

    override fun getFavourites(): Observable<List<ServiceLocationResponse>> {
        return Observable.combineLatest(
            getFavourites(Service.AIRCOACH),
            getFavourites(Service.BUS_EIREANN),
            getFavourites(Service.DUBLIN_BIKES),
            getFavourites(Service.DUBLIN_BUS),
            getFavourites(Service.IRISH_RAIL),
            getFavourites(Service.LUAS),
            Function6 { t1, t2, t3, t4, t5, t6 ->
                listOf(t1, t2, t3, t4, t5, t6)
            }
        )
//        return Observable.combineLatest(
//            enabledServiceManager.getEnabledServices().map { enabledService ->
//                serviceLocationRepositories.getValue(enabledService).getFavourites()
//                    .startWith(emptyList())
//                    .subscribeOn(Schedulers.newThread())
//            }
//        ) { serviceLocationStreams -> serviceLocationStreams.map { it as ServiceLocationResponse } }
    }

    private fun getFavourites(service: Service): Observable<ServiceLocationResponse> =
        if (enabledServiceManager.isServiceEnabled(service)) {
            serviceLocationRepositories.getValue(service).getFavourites()
                .startWith(ServiceLocationResponse.Data(service, emptyList()))
        } else {
            Observable.just(ServiceLocationResponse.Data(service, emptyList()))
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

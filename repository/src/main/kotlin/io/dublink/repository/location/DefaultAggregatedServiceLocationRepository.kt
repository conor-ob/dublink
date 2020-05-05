package io.dublink.repository.location

import io.dublink.domain.repository.AggregatedServiceLocationRepository
import io.dublink.domain.repository.ServiceLocationKey
import io.dublink.domain.repository.ServiceLocationRepository
import io.dublink.domain.repository.ServiceLocationResponse
import io.dublink.domain.service.EnabledServiceManager
import io.reactivex.Observable
import io.reactivex.functions.Function6
import io.rtpi.api.Service

class DefaultAggregatedServiceLocationRepository(
    private val serviceLocationRepositories: Map<Service, ServiceLocationRepository>,
    private val enabledServiceManager: EnabledServiceManager
) : AggregatedServiceLocationRepository {

    override fun get(): Observable<List<ServiceLocationResponse>> {
        return Observable.zip(
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
    }

    override fun stream(): Observable<List<ServiceLocationResponse>> {
        return Observable.combineLatest(
            stream(Service.AIRCOACH),
            stream(Service.BUS_EIREANN),
            stream(Service.DUBLIN_BIKES),
            stream(Service.DUBLIN_BUS),
            stream(Service.IRISH_RAIL),
            stream(Service.LUAS),
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
        } else {
            Observable.just(ServiceLocationResponse.Data(service, emptyList()))
        }

    private fun stream(service: Service): Observable<ServiceLocationResponse> =
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
    }

    override fun streamFavourites(): Observable<List<ServiceLocationResponse>> {
        return Observable.combineLatest(
            streamFavourites(Service.AIRCOACH),
            streamFavourites(Service.BUS_EIREANN),
            streamFavourites(Service.DUBLIN_BIKES),
            streamFavourites(Service.DUBLIN_BUS),
            streamFavourites(Service.IRISH_RAIL),
            streamFavourites(Service.LUAS),
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
        } else {
            Observable.just(ServiceLocationResponse.Data(service, emptyList()))
        }

    private fun streamFavourites(service: Service): Observable<ServiceLocationResponse> =
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

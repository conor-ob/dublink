package ie.dublinmapper.repository
//
// import com.nytimes.android.external.store3.base.impl.room.StoreRoom
// import ie.dublinmapper.domain.service.EnabledServiceManager
// import ie.dublinmapper.repository.location.DefaultAggregatedServiceLocationRepository
// import ie.dublinmapper.repository.location.DefaultServiceLocationRepository
// import io.reactivex.Observable
// import io.rtpi.api.*
// import org.junit.Test
//
// class DefaultAggregatedServiceLocationRepositoryTest {
//
//    private val aggregatedServiceLocationRepository =
//        DefaultAggregatedServiceLocationRepository(
//            locationRepositories = mapOf(
//                Service.IRISH_RAIL to DefaultServiceLocationRepository(
//                    Service.IRISH_RAIL,
//                    object : StoreRoom<List<ServiceLocation>, Service>() {
//                        override fun clear() {}
//                        override fun clear(key: Service) {}
//                        override fun fetch(key: Service): Observable<List<ServiceLocation>> {
//                            return get(key)
//                        }
//
//                        override fun get(key: Service): Observable<List<ServiceLocation>> {
//                            return Observable.just(
//                                listOf(
//                                    IrishRailStation(
//                                        id = "1",
//                                        name = "1",
//                                        coordinate = Coordinate(0.0, 0.0),
//                                        operators = setOf(Operator.DART),
//                                        routes = listOf(Route("DART", Operator.DART))
//                                    ),
//                                    IrishRailStation(
//                                        id = "2",
//                                        name = "2",
//                                        coordinate = Coordinate(0.0, 0.0),
//                                        operators = setOf(Operator.DART),
//                                        routes = listOf(Route("DART", Operator.DART))
//                                    )
//                                )
//                            )
//                        }
//                    }
//                ),
//                Service.DUBLIN_BUS to DefaultServiceLocationRepository(
//                    Service.DUBLIN_BUS,
//                    object : StoreRoom<List<ServiceLocation>, Service>() {
//                        override fun clear() {}
//                        override fun clear(key: Service) {}
//                        override fun fetch(key: Service): Observable<List<ServiceLocation>> {
//                            return get(key)
//                        }
//
//                        override fun get(key: Service): Observable<List<ServiceLocation>> {
//                            return Observable.just(
//                                listOf(
//                                    DublinBusStop(
//                                        id = "3",
//                                        name = "3",
//                                        coordinate = Coordinate(0.0, 0.0),
//                                        operators = setOf(Operator.DUBLIN_BUS),
//                                        routes = listOf(Route("3", Operator.DUBLIN_BUS))
//                                    ),
//                                    DublinBusStop(
//                                        id = "4",
//                                        name = "4",
//                                        coordinate = Coordinate(0.0, 0.0),
//                                        operators = setOf(Operator.DUBLIN_BUS),
//                                        routes = listOf(Route("4", Operator.DUBLIN_BUS))
//                                    )
//                                )
//                            )
//                        }
//                    }
//                )
//            ),
//            enabledServiceManager = object : EnabledServiceManager {
//                override fun enableService(service: Service) {
//
//                }
//
//                override fun isServiceEnabled(service: Service): Boolean {
//                    return service == Service.IRISH_RAIL || service == Service.DUBLIN_BUS
//                }
//
//                override fun getEnabledServices(): Set<Service> {
//                    return setOf(Service.IRISH_RAIL, Service.DUBLIN_BUS)
//                }
//
//            }
//        )
//
//    @Test
//    fun `test aggregate`() {
//        val blockingFirst = aggregatedServiceLocationRepository.get().blockingFirst()
//        blockingFirst.first()
//    }
// }

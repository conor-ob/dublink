package io.dublink.repository

import com.google.common.truth.Truth.assertThat
import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.model.DubLinkStopLocation
import io.dublink.domain.service.EnabledServiceManager
import io.dublink.repository.location.DefaultAggregatedServiceLocationRepository
import io.dublink.repository.location.DefaultServiceLocationRepository
import io.reactivex.Observable
import io.rtpi.api.Coordinate
import io.rtpi.api.Service
import io.rtpi.api.StopLocation
import org.junit.Test

class DefaultAggregatedServiceLocationRepositoryTest {

    private val aggregatedServiceLocationRepository = DefaultAggregatedServiceLocationRepository(
        serviceLocationRepositories = mapOf(
            Service.IRISH_RAIL to DefaultServiceLocationRepository(
                Service.IRISH_RAIL,
                object : StoreRoom<List<DubLinkServiceLocation>, Service>() {
                    override fun clear() {}
                    override fun clear(key: Service) {}
                    override fun fetch(key: Service): Observable<List<DubLinkServiceLocation>> {
                        return get(key)
                    }

                    override fun get(key: Service): Observable<List<DubLinkServiceLocation>> {
                        return Observable.just(
                            listOf(
                                DubLinkStopLocation(
                                    stopLocation = StopLocation(
                                        id = "1",
                                        name = "1",
                                        service = Service.IRISH_RAIL,
                                        routeGroups = emptyList(),
                                        coordinate = Coordinate(0.0, 0.0)
                                    )
                                ),
                                DubLinkStopLocation(
                                    stopLocation = StopLocation(
                                        id = "2",
                                        name = "2",
                                        service = Service.IRISH_RAIL,
                                        routeGroups = emptyList(),
                                        coordinate = Coordinate(0.0, 0.0)
                                    )
                                )
                            )
                        )
                    }
                }
            ),
            Service.DUBLIN_BUS to DefaultServiceLocationRepository(
                Service.DUBLIN_BUS,
                object : StoreRoom<List<DubLinkServiceLocation>, Service>() {
                    override fun clear() {}
                    override fun clear(key: Service) {}
                    override fun fetch(key: Service): Observable<List<DubLinkServiceLocation>> {
                        return get(key)
                    }

                    override fun get(key: Service): Observable<List<DubLinkServiceLocation>> {
                        return Observable.just(
                            listOf(
                                DubLinkStopLocation(
                                    stopLocation = StopLocation(
                                        id = "3",
                                        name = "3",
                                        service = Service.DUBLIN_BUS,
                                        routeGroups = emptyList(),
                                        coordinate = Coordinate(0.0, 0.0)
                                    )
                                ),
                                DubLinkStopLocation(
                                    stopLocation = StopLocation(
                                        id = "4",
                                        name = "4",
                                        service = Service.DUBLIN_BUS,
                                        routeGroups = emptyList(),
                                        coordinate = Coordinate(0.0, 0.0)
                                    )
                                )
                            )
                        )
                    }
                }
            )
        ),
        enabledServiceManager = object : EnabledServiceManager {
            override fun enableService(service: Service): Boolean {
                return true
            }

            override fun isServiceEnabled(service: Service): Boolean {
                return service == Service.IRISH_RAIL || service == Service.DUBLIN_BUS
            }

            override fun getEnabledServices(): List<Service> {
                return listOf(Service.IRISH_RAIL, Service.DUBLIN_BUS)
            }
        }
    )

    @Test
    fun `test aggregate`() {
        val blockingFirst = aggregatedServiceLocationRepository.get(refresh = false).blockingFirst()
        val serviceLocations = blockingFirst.serviceLocations

        assertThat(serviceLocations.map { it.id }).containsExactly(
            "1", "2", "3", "4"
        )
    }
}

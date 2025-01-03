package io.dublink.favourites

import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.repository.FavouriteRepository
import io.dublink.domain.repository.LiveDataKey
import io.dublink.domain.repository.LiveDataRepository
import io.dublink.domain.repository.LiveDataResponse
import io.dublink.domain.service.LocationProvider
import io.dublink.domain.service.PermissionChecker
import io.dublink.domain.service.PreferenceStore
import io.dublink.domain.service.RxScheduler
import io.dublink.domain.util.LiveDataFilter
import io.dublink.domain.util.haversine
import io.reactivex.Observable
import io.rtpi.api.LiveData
import io.rtpi.util.LiveDataGrouper
import javax.inject.Inject

class FavouritesUseCase @Inject constructor(
    private val favouriteRepository: FavouriteRepository,
    private val liveDataRepository: LiveDataRepository,
    private val permissionChecker: PermissionChecker,
    private val locationProvider: LocationProvider,
    private val preferenceStore: PreferenceStore,
    private val scheduler: RxScheduler
) {

    fun getFavourites(): Observable<List<ServiceLocationPresentationResponse>> =
        if (preferenceStore.isFavouritesSortByLocation() &&
            permissionChecker.isLocationPermissionGranted()
        ) {
            locationProvider.getLocationUpdates(thresholdDistance = 25.0)
                .flatMap { coordinate ->
                    favouriteRepository.getFavourites()
                        .map { responses ->
                            responses
                                .sortedWith(
                                    Comparator { s1, s2 ->
                                        s1.coordinate.haversine(coordinate).compareTo(s2.coordinate.haversine(coordinate))
                                    }
                                )
                                .map { serviceLocation ->
                                    ServiceLocationPresentationResponse(
                                        serviceLocation = serviceLocation,
                                        distance = coordinate.haversine(serviceLocation.coordinate)
                                    )
                                }
                        }
                }
        } else {
            favouriteRepository.getFavourites()
                .map { responses ->
                    responses
                        .sortedWith(
                            Comparator { s1, s2 ->
                                s1.favouriteSortIndex.compareTo(s2.favouriteSortIndex)
                            }
                        )
                        .map { serviceLocation ->
                            ServiceLocationPresentationResponse(
                                serviceLocation = serviceLocation,
                                distance = null
                            )
                        }
                }
        }

    fun getLiveData(refresh: Boolean): Observable<List<LiveDataPresentationResponse>> {
        val limit = preferenceStore.getFavouritesLiveDataLimit()
        return getFavourites()
            .map { favourites -> favourites.map { it.serviceLocation } }
            .flatMap { serviceLocations ->
                Observable.combineLatest(
                    serviceLocations.mapIndexed { index, dubLinkServiceLocation ->
                        if (index < limit) {
                            getGroupedLiveData(dubLinkServiceLocation, refresh)
                                .startWith(
                                    LiveDataPresentationResponse.Loading(dubLinkServiceLocation)
                                )
                                .subscribeOn(scheduler.io)
                        } else {
                            Observable.just(
                                LiveDataPresentationResponse.Skipped(
                                    serviceLocation = dubLinkServiceLocation
                                )
                            )
                        }
                    }
                ) { streams -> streams.map { it as LiveDataPresentationResponse } }
            }
    }

    private fun getGroupedLiveData(
        serviceLocation: DubLinkServiceLocation,
        refresh: Boolean
    ): Observable<LiveDataPresentationResponse> =
        liveDataRepository.get(
            LiveDataKey(
                service = serviceLocation.service,
                locationId = serviceLocation.id
            ),
            refresh
        ).map { response ->
            when (response) {
                is LiveDataResponse.Data -> LiveDataPresentationResponse.Data(
                    serviceLocation = serviceLocation,
                    liveData = LiveDataGrouper.groupLiveData(
                        LiveDataFilter.filterLiveData(
                            serviceLocation,
                            response.liveData
                        )
                    )
                )
                is LiveDataResponse.Error -> LiveDataPresentationResponse.Error(
                    serviceLocation = serviceLocation,
                    throwable = response.throwable
                )
            }
        }
}

data class ServiceLocationPresentationResponse(
    val serviceLocation: DubLinkServiceLocation,
    val distance: Double?
)

sealed class LiveDataPresentationResponse {

    abstract val serviceLocation: DubLinkServiceLocation

    data class Loading(
        override val serviceLocation: DubLinkServiceLocation
    ) : LiveDataPresentationResponse()

    data class Skipped(
        override val serviceLocation: DubLinkServiceLocation
    ) : LiveDataPresentationResponse()

    data class Data(
        override val serviceLocation: DubLinkServiceLocation,
        val liveData: List<List<LiveData>>
    ) : LiveDataPresentationResponse()

    data class Error(
        override val serviceLocation: DubLinkServiceLocation,
        val throwable: Throwable
    ) : LiveDataPresentationResponse()
}

package io.dublink.favourites

import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.repository.AggregatedServiceLocationRepository
import io.dublink.domain.repository.FavouriteRepository
import io.dublink.domain.repository.LiveDataKey
import io.dublink.domain.repository.LiveDataRepository
import io.dublink.domain.repository.LiveDataResponse
import io.dublink.domain.repository.ServiceLocationResponse
import io.dublink.domain.service.LocationProvider
import io.dublink.domain.service.PermissionChecker
import io.dublink.domain.service.PreferenceStore
import io.dublink.domain.util.LiveDataFilter
import io.dublink.domain.util.haversine
import io.reactivex.Observable
import io.rtpi.api.LiveData
import io.rtpi.util.LiveDataGrouper
import javax.inject.Inject

class FavouritesUseCase @Inject constructor(
    private val favouriteRepository: FavouriteRepository,
    private val serviceLocationRepository: AggregatedServiceLocationRepository,
    private val liveDataRepository: LiveDataRepository,
    private val permissionChecker: PermissionChecker,
    private val locationProvider: LocationProvider,
    private val preferenceStore: PreferenceStore
) {

    fun getFavourites(): Observable<List<DubLinkServiceLocation>> =
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
                }
        }

    private fun getFavouriteServiceLocations(): Observable<List<DubLinkServiceLocation>> =
        if (preferenceStore.isFavouritesSortByLocation() &&
            permissionChecker.isLocationPermissionGranted()
        ) {
            locationProvider.getLocationUpdates(thresholdDistance = 25.0)
                .flatMap { coordinate ->
                    serviceLocationRepository.streamFavourites()
                        .map { responses ->
                            responses
                                .filterIsInstance<ServiceLocationResponse.Data>()
                                .flatMap { it.serviceLocations }
                                .sortedWith(
                                    Comparator { s1, s2 ->
                                        s1.coordinate.haversine(coordinate).compareTo(s2.coordinate.haversine(coordinate))
                                    }
                                )
                        }
                }
        } else {
            serviceLocationRepository.streamFavourites()
                .map { responses ->
                    responses
                        .filterIsInstance<ServiceLocationResponse.Data>()
                        .flatMap { it.serviceLocations }
                        .sortedWith(
                            Comparator { s1, s2 ->
                                s1.favouriteSortIndex.compareTo(s2.favouriteSortIndex)
                            }
                        )
                }
        }

    fun getLiveData(refresh: Boolean): Observable<List<LiveDataPresentationResponse>> {
        val limit = preferenceStore.getFavouritesLiveDataLimit()
        return getFavouriteServiceLocations()
            .flatMap { serviceLocations ->
                Observable.combineLatest(
                    serviceLocations.mapIndexed { index, dubLinkServiceLocation ->
                        if (index < limit) {
                            getGroupedLiveData(dubLinkServiceLocation, refresh)
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

sealed class LiveDataPresentationResponse {

    abstract val serviceLocation: DubLinkServiceLocation

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

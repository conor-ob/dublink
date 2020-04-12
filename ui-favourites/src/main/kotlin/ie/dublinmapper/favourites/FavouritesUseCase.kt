package ie.dublinmapper.favourites

import ie.dublinmapper.domain.repository.*
import ie.dublinmapper.domain.service.LocationProvider
import ie.dublinmapper.domain.service.PermissionChecker
import ie.dublinmapper.domain.service.PreferenceStore
import ie.dublinmapper.domain.util.haversine
import io.reactivex.Observable
import io.rtpi.api.LiveData
import io.rtpi.api.ServiceLocation
import io.rtpi.util.LiveDataGrouper
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class FavouritesUseCase @Inject constructor(
    private val serviceLocationRepository: AggregatedServiceLocationRepository,
    private val liveDataRepository: LiveDataRepository,
    private val permissionChecker: PermissionChecker,
    private val locationProvider: LocationProvider,
    private val preferenceStore: PreferenceStore
) {

    fun getFavouritesWithLiveData(showLoading: Boolean): Observable<List<LiveDataPresentationResponse>> {
        if (preferenceStore.isFavouritesSortByLocation() && permissionChecker.isLocationPermissionGranted()) {
            return locationProvider.getLocationUpdates(25.0)
                .flatMap { coordinate ->
                    getFavouritesWithLiveData(
                        showLoading = showLoading,
                        comparator = Comparator { s1, s2 ->
                            s1.coordinate.haversine(coordinate)
                                .compareTo(s2.coordinate.haversine(coordinate))
                        }
                    )
                }
        } else {
            return getFavouritesWithLiveData(
                showLoading = showLoading,
                comparator = Comparator { s1, s2 -> s1.name.compareTo(s2.name) }
            )
        }
    }

    private fun getFavouritesWithLiveData(
        showLoading: Boolean,
        comparator: Comparator<ServiceLocation>
    ): Observable<List<LiveDataPresentationResponse>> {
        val limit = preferenceStore.getFavouritesLiveDataLimit()
        return serviceLocationRepository.getFavourites()
            .flatMap { responses ->
                Observable.combineLatest(
                    responses
                        .filterIsInstance<ServiceLocationResponse.Data>()
                        .flatMap { it.serviceLocations }
                        .sortedWith(comparator)
                        .mapIndexed { index: Int, serviceLocation: ServiceLocation ->
                            if (index < limit) {
                                if (showLoading) {
                                    getGroupedLiveData(serviceLocation)
                                        .startWith(
                                            LiveDataPresentationResponse.Loading(
                                                serviceLocation = serviceLocation
                                            )
                                        )
                                } else {
                                    getGroupedLiveData(serviceLocation)
                                }
                            } else {
                                Observable.just(
                                    LiveDataPresentationResponse.Skipped(
                                        serviceLocation = serviceLocation
                                    )
                                )
                            }
                        }
                ) { streams -> streams.map { it as LiveDataPresentationResponse } }
            }
    }

    private fun getGroupedLiveData(
        serviceLocation: ServiceLocation
    ): Observable<LiveDataPresentationResponse> {
        return Observable
            .interval(0L, preferenceStore.getLiveDataRefreshInterval(), TimeUnit.SECONDS)
            .flatMap {
                liveDataRepository.get(
                    LiveDataKey(
                        service = serviceLocation.service,
                        locationId = serviceLocation.id
                    )
                ).map { response ->
                    when (response) {
                        is LiveDataResponse.Data -> LiveDataPresentationResponse.Data(
                            serviceLocation = serviceLocation,
                            liveData = LiveDataGrouper.groupLiveData(response.liveData)
                        )
                        is LiveDataResponse.Error -> LiveDataPresentationResponse.Error(
                            serviceLocation = serviceLocation,
                            throwable = response.throwable
                        )
                    }
                }
            }
    }
}

sealed class LiveDataPresentationResponse {

    abstract val serviceLocation: ServiceLocation

    data class Loading(
        override val serviceLocation: ServiceLocation
    ) : LiveDataPresentationResponse()

    data class Skipped(
        override val serviceLocation: ServiceLocation
    ) : LiveDataPresentationResponse()

    data class Data(
        override val serviceLocation: ServiceLocation,
        val liveData: List<List<LiveData>>
    ) : LiveDataPresentationResponse()

    data class Error(
        override val serviceLocation: ServiceLocation,
        val throwable: Throwable
    ) : LiveDataPresentationResponse()
}

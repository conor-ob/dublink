package ie.dublinmapper.favourites

import ie.dublinmapper.domain.model.DubLinkServiceLocation
import ie.dublinmapper.domain.repository.AggregatedServiceLocationRepository
import ie.dublinmapper.domain.repository.LiveDataKey
import ie.dublinmapper.domain.repository.LiveDataRepository
import ie.dublinmapper.domain.repository.LiveDataResponse
import ie.dublinmapper.domain.repository.ServiceLocationResponse
import ie.dublinmapper.domain.service.LocationProvider
import ie.dublinmapper.domain.service.PermissionChecker
import ie.dublinmapper.domain.service.PreferenceStore
import ie.dublinmapper.domain.util.LiveDataFilter
import ie.dublinmapper.domain.util.haversine
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.rtpi.api.LiveData
import io.rtpi.util.LiveDataGrouper
import javax.inject.Inject

class FavouritesUseCase @Inject constructor(
    private val serviceLocationRepository: AggregatedServiceLocationRepository,
    private val liveDataRepository: LiveDataRepository,
    private val permissionChecker: PermissionChecker,
    private val locationProvider: LocationProvider,
    private val preferenceStore: PreferenceStore
) {

    fun getFavouritesWithLiveData(
        showLoading: Boolean
    ): Observable<List<LiveDataPresentationResponse>> {
        if (preferenceStore.isFavouritesSortByLocation() &&
            permissionChecker.isLocationPermissionGranted()
        ) {
            return locationProvider.getLocationUpdates(thresholdDistance = 25.0)
                .flatMap { coordinate ->
                    getFavouritesWithLiveData(
                        showLoading = showLoading,
                        comparator = Comparator { s1, s2 ->
                            s1.coordinate.haversine(coordinate).compareTo(s2.coordinate.haversine(coordinate))
                        }
                    )
                }
        } else {
            return getFavouritesWithLiveData(
                showLoading = showLoading,
                comparator = Comparator { s1, s2 ->
                    s1.favouriteSortIndex.compareTo(s2.favouriteSortIndex)
                }
            )
        }
    }

    private fun getFavouritesWithLiveData(
        showLoading: Boolean,
        comparator: Comparator<DubLinkServiceLocation>
    ): Observable<List<LiveDataPresentationResponse>> {
        val limit = preferenceStore.getFavouritesLiveDataLimit()
        return serviceLocationRepository.getFavourites()
            .flatMap { responses ->
                Observable.combineLatest(
                    responses
                        .filterIsInstance<ServiceLocationResponse.Data>()
                        .flatMap { it.serviceLocations }
                        .sortedWith(comparator)
                        .mapIndexed { index: Int, serviceLocation: DubLinkServiceLocation ->
                            if (index < limit) {
                                // TODO this may temporarily cause blank screen when navigating back to favourites
                                if (showLoading) {
                                    getGroupedLiveData(serviceLocation)
                                        .startWith(
                                            LiveDataPresentationResponse.Loading(
                                                serviceLocation = serviceLocation
                                            )
                                        )
                                        .subscribeOn(Schedulers.newThread())
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
        serviceLocation: DubLinkServiceLocation
    ): Observable<LiveDataPresentationResponse> =
        liveDataRepository.get(
            LiveDataKey(
                service = serviceLocation.service,
                locationId = serviceLocation.id
            )
        ).map { response ->
            when (response) {
                is LiveDataResponse.Data -> LiveDataPresentationResponse.Data(
                    serviceLocation = serviceLocation,
                    liveData = LiveDataGrouper.groupLiveData(LiveDataFilter.filterLiveData(serviceLocation, response.liveData))
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

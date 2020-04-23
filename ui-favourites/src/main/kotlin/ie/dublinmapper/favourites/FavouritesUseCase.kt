package ie.dublinmapper.favourites

import ie.dublinmapper.domain.model.getSortIndex
import ie.dublinmapper.domain.repository.AggregatedServiceLocationRepository
import ie.dublinmapper.domain.repository.FavouriteRepository
import ie.dublinmapper.domain.repository.LiveDataKey
import ie.dublinmapper.domain.repository.LiveDataRepository
import ie.dublinmapper.domain.repository.LiveDataResponse
import ie.dublinmapper.domain.repository.ServiceLocationResponse
import ie.dublinmapper.domain.service.LocationProvider
import ie.dublinmapper.domain.service.PermissionChecker
import ie.dublinmapper.domain.service.PreferenceStore
import ie.dublinmapper.domain.util.haversine
import io.reactivex.Observable
import io.rtpi.api.LiveData
import io.rtpi.api.ServiceLocation
import io.rtpi.util.LiveDataGrouper
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class FavouritesUseCase @Inject constructor(
    private val favouriteRepository: FavouriteRepository,
    private val serviceLocationRepository: AggregatedServiceLocationRepository,
    private val liveDataRepository: LiveDataRepository,
    private val permissionChecker: PermissionChecker,
    private val locationProvider: LocationProvider,
    private val preferenceStore: PreferenceStore
) {

    fun getFavouritesWithLiveData(
        showLoading: Boolean
//        streamOpen: AtomicBoolean
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
//                        streamOpen = streamOpen
                    )
                }
        } else {
            return getFavouritesWithLiveData(
                showLoading = showLoading,
                comparator = Comparator { s1, s2 ->
                    s1.getSortIndex().compareTo(s2.getSortIndex())
                }
//                streamOpen = streamOpen
            )
        }
    }

    private fun getFavouritesWithLiveData(
        showLoading: Boolean,
        comparator: Comparator<ServiceLocation>
//        streamOpen: AtomicBoolean
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
                                // TODO this may temporarily cause blank screen when navigating back to favourites
                                if (showLoading) {
                                    getGroupedLiveData(serviceLocation)
//                                    getGroupedLiveData(serviceLocation, streamOpen)
                                        .startWith(
                                            LiveDataPresentationResponse.Loading(
                                                serviceLocation = serviceLocation
                                            )
                                        )
                                } else {
                                    getGroupedLiveData(serviceLocation)
//                                    getGroupedLiveData(serviceLocation, streamOpen)
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
//        streamOpen: AtomicBoolean
    ): Observable<LiveDataPresentationResponse> {
        return Observable
            .interval(0L, preferenceStore.getLiveDataRefreshInterval(), TimeUnit.SECONDS)
//            .filter { streamOpen.get() }
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

    fun nameToBeDetermined(serviceLocation: ServiceLocation): Observable<Boolean> {
        return Observable.fromCallable {
            favouriteRepository.nameToBeDetermined(serviceLocation)
            serviceLocationRepository.clearAllCaches()
            return@fromCallable true
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

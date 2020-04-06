package ie.dublinmapper.favourites

import ie.dublinmapper.domain.repository.*
import ie.dublinmapper.domain.service.PreferenceStore
import io.reactivex.Observable
import io.rtpi.api.LiveData
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation
import io.rtpi.util.LiveDataGrouper
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class FavouritesUseCase @Inject constructor(
    private val serviceLocationRepository: AggregatedServiceLocationRepository,
    private val liveDataRepository: AggregatedLiveDataRepository,
    private val preferenceStore: PreferenceStore
) {

    fun getFavourites(): Observable<FavouritesPresentationResponse> {
        return serviceLocationRepository.getFavourites()
            .map { responses ->
                val data = responses
                    .filterIsInstance<ServiceLocationResponse.Data>()
                    .flatMap { it.serviceLocations }
                FavouritesPresentationResponse(data)
            }
    }

//    fun getFavourites(): Observable<FavouriteServiceLocationsPresentationResponse> {
////        val favouriteServiceLocationResponses = serviceLocationRepository.getFavourites().blockingFirst()
////        val favourites = favouriteServiceLocationResponses
////            .filterIsInstance<ServiceLocationResponse.Data>()
////            .flatMap { it.serviceLocations }
//////            .sortedBy { it.id } sort by order or proximity
////        val servicesWithErrors = favouriteServiceLocationResponses
////            .filterIsInstance<ServiceLocationResponse.Error>()
////            .map { it.service }
//
//        val limit = preferenceStore.getFavouritesLiveDataLimit()
//
//
//        return serviceLocationRepository.getFavourites()
//            .flatMap { responses ->
//                Observable.combineLatest(
//                    responses
//                        .filterIsInstance<ServiceLocationResponse.Data>()
//                        .flatMap { it.serviceLocations }
////                    .sortedBy { it.id }
//                        .mapIndexed { index: Int, serviceLocation: ServiceLocation ->
//                            if (index < limit) {
//                                getGroupedLiveData(serviceLocation)
//                                    .startWith(
//                                        LiveDataPresentationResponse.Loading(
//                                            serviceLocation = serviceLocation
//                                        )
//                                    )
//                            } else {
//                                Observable.just(
//                                    LiveDataPresentationResponse.Skipped(
//                                        serviceLocation = serviceLocation
//                                    )
//                                )
//                            }
//                        }
//                ) { ldr ->
//                    val resp = FavouriteServiceLocationsPresentationResponse(
//                        ldr.map { it as LiveDataPresentationResponse }
//                    )
//                    Timber.d("HERE %s", resp.toString())
//                    resp
//                }
//            }

//        return getLiveDataForFavourites(favourites, servicesWithErrors)


//        if (permissionChecker.isLocationPermissionGranted()) {
//            return getLiveDataForFavourites(favourites.sortedBy { Coordinate(53.3010, -6.1852).haversine(it.coordinate) })
//            return Observable.concat(
//                locationProvider.getLastKnownLocation(),
//                locationProvider.getLocationUpdates()
//            )
//                .throttleFirst(30, TimeUnit.SECONDS)
//                .flatMap { coordinate ->
//                    getFavouritesWithLiveData(favourites.sortedBy { coordinate.haversine(it.coordinate) })
//                }.startWith {
//                    getFavouritesWithLiveData(favourites)
//                }
//                .throttleFirst(30, TimeUnit.SECONDS),
//                BiFunction { serviceLocations, coordinate ->
//                    FavouritesResponse(
//                        serviceLocations.sortedBy {
//                            LocationUtils.haversineDistance(coordinate, it.coordinate)
//                        }
//                    )
//                }
//        } else {

//        }
//    }

    fun getLiveData(): Observable<List<LiveDataPresentationResponse>> {
        val limit = preferenceStore.getFavouritesLiveDataLimit()
        return serviceLocationRepository.getFavourites()
            .flatMap { responses ->
                Observable.combineLatest(
                    responses
                        .filterIsInstance<ServiceLocationResponse.Data>()
                        .flatMap { it.serviceLocations }
//                    .sortedBy { it.id }
                        .mapIndexed { index: Int, serviceLocation: ServiceLocation ->
                            if (index < limit) {
                                getGroupedLiveData(serviceLocation)
                                    .startWith(
                                        LiveDataPresentationResponse.Loading(
                                            serviceLocation = serviceLocation
                                        )
                                    )
                            } else {
                                Observable.just(
                                    LiveDataPresentationResponse.Skipped(
                                        serviceLocation = serviceLocation
                                    )
                                )
                            }
                        }
                ) { ldr ->
                    ldr.map { it as LiveDataPresentationResponse }
                }
            }
    }

    private fun getGroupedLiveData(serviceLocation: ServiceLocation): Observable<LiveDataPresentationResponse> {
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

    private fun getLiveDataForFavourites(
        favourites: List<ServiceLocation>,
        servicesWithErrors: List<Service> // TODO
    ): Observable<FavouritesPresentationResponse> {
        TODO()
//        val limit = preferenceStore.getFavouritesLiveDataLimit()
//        return Observable.combineLatest(
//            favourites.mapIndexed { index, serviceLocation ->
//                if (index < limit) {
//                    liveDataUseCase.getGroupedLiveDataStream(serviceLocation).startWith(
//                        ie.dublinmapper.favourites.LiveDataResponse.Loading(
//                            serviceLocation
//                        )
//                    )
//                } else {
//                    Observable.just(
//                        ie.dublinmapper.favourites.LiveDataResponse.Skipped(
//                            serviceLocation
//                        )
//                    )
//                }
//            }
//        ) { responses -> ie.dublinmapper.favourites.FavouritesResponse(responses.map { it as ie.dublinmapper.favourites.LiveDataResponse }) }
    }





}

data class FavouritesPresentationResponse(
    val favourites: List<ServiceLocation>
)

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

package ie.dublinmapper.domain.usecase

import ie.dublinmapper.domain.repository.FavouriteRepository
import ie.dublinmapper.domain.repository.LocationRepository
import ie.dublinmapper.domain.service.LocationProvider
import ie.dublinmapper.domain.service.PermissionChecker
import ie.dublinmapper.domain.service.PreferenceStore
import ie.dublinmapper.domain.util.haversine
import io.reactivex.Observable
import io.rtpi.api.Coordinate
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation
import javax.inject.Inject
import javax.inject.Named

class FavouritesUseCase @Inject constructor(
    private val favouriteRepository: FavouriteRepository,
    @Named("SERVICE_LOCATION") private val locationRepository: LocationRepository,
    private val permissionChecker: PermissionChecker,
    private val locationProvider: LocationProvider,
    private val liveDataUseCase: LiveDataUseCase,
    private val preferenceStore: PreferenceStore
) {

    fun getFavourites(): Observable<FavouritesResponse> {
        val favourites = locationRepository.getFavourites().blockingFirst()
        if (permissionChecker.isLocationPermissionGranted()) {
            return getLiveDataForFavourites(favourites.sortedBy { Coordinate(53.3010, -6.1852).haversine(it.coordinate) })
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
        } else {
            return getLiveDataForFavourites(favourites)
        }
    }

    private fun getLiveDataForFavourites(
        favourites: List<ServiceLocation>
    ): Observable<FavouritesResponse> {
        val limit = preferenceStore.getFavouritesLiveDataLimit()
        return Observable.combineLatest(
            favourites.mapIndexed { index, serviceLocation ->
                if (index < limit) {
                    liveDataUseCase.getGroupedLiveDataStream(serviceLocation)
                } else {
                    Observable.just(LiveDataResponse.Skipped(serviceLocation))
                }
            }
        ) { responses -> FavouritesResponse(responses.map { it as LiveDataResponse }) }
    }

    private fun clearServiceLocationCache(service: Service) {
        locationRepository.clearCache(service)
    }

    fun saveFavourite(serviceLocationId: String, serviceLocationName: String, service: Service): Observable<Boolean> {
        return Observable.fromCallable {
            clearServiceLocationCache(service)
            favouriteRepository.saveFavourite(serviceLocationId, serviceLocationName, service)
            return@fromCallable true
        }
    }

    fun removeFavourite(serviceLocationId: String, service: Service): Observable<Boolean> {
        return Observable.fromCallable {
            clearServiceLocationCache(service)
            favouriteRepository.removeFavourite(serviceLocationId, service)
            return@fromCallable true
        }
    }

}

data class FavouritesResponse(
    val liveDataResponses: List<LiveDataResponse>
)

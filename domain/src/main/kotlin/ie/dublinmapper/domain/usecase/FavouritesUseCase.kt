package ie.dublinmapper.domain.usecase

import ie.dublinmapper.domain.repository.FavouriteRepository
import ie.dublinmapper.domain.repository.LocationRepository
import ie.dublinmapper.domain.service.LocationProvider
import ie.dublinmapper.domain.service.PermissionChecker
import io.reactivex.Observable
import io.reactivex.functions.Function
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation
import javax.inject.Inject
import javax.inject.Named

class FavouritesUseCase @Inject constructor(
    private val favouriteRepository: FavouriteRepository,
    @Named("SERVICE_LOCATION") private val locationRepository: LocationRepository,
    private val permissionChecker: PermissionChecker,
    private val locationProvider: LocationProvider,
    private val liveDataUseCase: LiveDataUseCase
) {

    fun getFavourites(): Observable<FavouritesResponse> {
//        if (permissionChecker.isLocationPermissionGranted()) {
//            return Observable.zip(
//                getFavouriteServiceLocations(),
//                Observable.concat(
//                    locationProvider.getLastKnownLocation(),
//                    locationProvider.getLocationUpdates()
//                )
//                    .throttleFirst(30, TimeUnit.SECONDS),
//                BiFunction { serviceLocations, coordinate ->
//                    FavouritesResponse(
//                        serviceLocations.sortedBy {
//                            LocationUtils.haversineDistance(coordinate, it.coordinate)
//                        }
//                    )
//                }
//            )
//        }
        val favs = getFavouriteServiceLocations().blockingFirst()
        return getFavouritesWithLiveData(favs)
//        return Observable.concat(
//            getRawFavourites(favs),

//            getFavouriteServiceLocations().map { favourites ->
//                FavouritesResponse(favourites.associateWith {
//                    liveDataUseCase.getLiveDataStream(it.id, it.name, it.service).blockingFirst().liveData.take(3)
//                })
//            }
//        )
//        return getFavouriteServiceLocations().map { favourites ->
//            FavouritesResponse(favourites.associateWith {
//                liveDataUseCase.getLiveDataStream(it.id, it.name, it.service).blockingFirst().liveData.take(3)
//            })
//        }
    }

    private fun getFavouritesWithLiveData(favs: List<ServiceLocation>): Observable<FavouritesResponse> {
        return Observable.combineLatest(
            favs.map {
                liveDataUseCase.getLiveDataStream(it.id, it.name, it.service).startWith(
                    LiveDataResponse(it.service, it.name, emptyList(), State.LOADING)
                )
            },
            Function { thing ->
                val responses = thing.map {
                    val resp = it as LiveDataResponse
                    LiveDataResponse(
                        service = resp.service,
                        serviceLocationName = resp.serviceLocationName,
                        liveData = resp.liveData.take(3),
                        state = resp.state
                    )
                }.associateBy { it.serviceLocationName }
                val favourites = favs.associateBy { it.name }
                val newMap = mutableMapOf<ServiceLocation, LiveDataResponse>()
                for (entry in favourites) {
                    newMap[entry.value] = responses[entry.key]!!
                }
                return@Function FavouritesResponse(
                    newMap
                )
            }
        )
    }

    private fun getFavouriteServiceLocations(): Observable<List<ServiceLocation>> {
        return locationRepository.getFavourites()
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
    val serviceLocations: Map<ServiceLocation, LiveDataResponse>
)

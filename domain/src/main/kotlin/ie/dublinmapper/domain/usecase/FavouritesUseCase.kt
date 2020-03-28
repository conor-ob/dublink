package ie.dublinmapper.domain.usecase

import ie.dublinmapper.domain.repository.FavouriteRepository
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.domain.service.LocationProvider
import ie.dublinmapper.domain.service.PermissionChecker
import io.reactivex.Observable
import io.reactivex.functions.Function6
import io.rtpi.api.*
import javax.inject.Inject

class FavouritesUseCase @Inject constructor(
    private val favouriteRepository: FavouriteRepository,
    private val aircoachStopRepository: Repository<AircoachStop>,
    private val busEireannStopRepository: Repository<BusEireannStop>,
    private val irishRailStationRepository: Repository<IrishRailStation>,
    private val dublinBikesDockRepository: Repository<DublinBikesDock>,
    private val dublinBusStopRepository: Repository<DublinBusStop>,
    private val luasStopRepository: Repository<LuasStop>,
    private val permissionChecker: PermissionChecker,
    private val locationProvider: LocationProvider,
    private val liveDataUseCase: LiveDataUseCase
) {

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
//        return Observable.concat(
//            getFavouriteServiceLocations().map {
//                FavouritesResponse(it.associateWith { emptyList<LiveData>() })
//            },
//            getFavouriteServiceLocations().map { favourites ->
//                FavouritesResponse(favourites.associateWith {
//                    liveDataUseCase.getLiveDataStream(it.id, it.name, it.service).blockingFirst().liveData.take(3)
//                })
//            }
//        )
        return getFavouriteServiceLocations().map { favourites ->
            FavouritesResponse(favourites.associateWith {
                liveDataUseCase.getLiveDataStream(it.id, it.name, it.service).blockingFirst().liveData.take(3)
            })
        }
    }

    private fun getFavouriteServiceLocations(): Observable<List<ServiceLocation>> {
        return Observable.combineLatest(
            aircoachStopRepository.getAllFavorites(),
            busEireannStopRepository.getAllFavorites(),
            dublinBikesDockRepository.getAllFavorites(),
            dublinBusStopRepository.getAllFavorites(),
            irishRailStationRepository.getAllFavorites(),
            luasStopRepository.getAllFavorites(),
            Function6 {
                    aircoachStops,
                    busEireannStops,
                    dublinBikesDocks,
                    dublinBusStops,
                    irishRailStations,
                    luasStops ->
                aircoachStops
                    .asSequence()
                    .plus(busEireannStops)
                    .plus(dublinBikesDocks)
                    .plus(dublinBusStops)
                    .plus(irishRailStations)
                    .plus(luasStops)
                    .toList()
            }
        )
    }

    private fun clearServiceLocationCache(service: Service) {
        when (service) {
            Service.AIRCOACH -> aircoachStopRepository.clearCache()
            Service.BUS_EIREANN -> busEireannStopRepository.clearCache()
            Service.DUBLIN_BIKES -> dublinBikesDockRepository.clearCache()
            Service.DUBLIN_BUS -> dublinBusStopRepository.clearCache()
            Service.IRISH_RAIL -> irishRailStationRepository.clearCache()
            Service.LUAS -> luasStopRepository.clearCache()
        }
    }

}

data class FavouritesResponse(
    val serviceLocations: Map<ServiceLocation, List<LiveData>>
)

package ie.dublinmapper.domain.usecase

import ie.dublinmapper.domain.repository.FavouriteRepository
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.domain.service.LocationProvider
import ie.dublinmapper.domain.service.PermissionChecker
import io.reactivex.Observable
import io.reactivex.functions.Function
import io.reactivex.functions.Function3
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

//    private fun getRawFavourites(favs: List<ServiceLocation>): Observable<FavouritesResponse> {
//        return Observable.just(
//            FavouritesResponse(favs.associateWith { emptyList<LiveData>() })
//        )
//    }

    private fun getFavouritesWithLiveData(favs: List<ServiceLocation>): Observable<FavouritesResponse> {
        return Observable.combineLatest(
            favs.map {
                liveDataUseCase.getLiveDataStream(it.id, it.name, it.service).startWith(
                    LiveDataResponse(it.service, it.name, emptyList(), State.LOADING)
                )
            },
            Function { thing ->
                val responses = thing.map { it as LiveDataResponse }.associateBy { it.serviceLocationName }
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

//        return Observable.combineLatest(
//            liveDataUseCase.getLiveDataStream(favs[0].id, favs[0].name, favs[0].service),
//            liveDataUseCase.getLiveDataStream(favs[1].id, favs[1].name, favs[1].service),
//            liveDataUseCase.getLiveDataStream(favs[2].id, favs[2].name, favs[2].service),
//            liveDataUseCase.getLiveDataStream(favs[3].id, favs[3].name, favs[3].service),
//            liveDataUseCase.getLiveDataStream(favs[4].id, favs[4].name, favs[4].service),
//            liveDataUseCase.getLiveDataStream(favs[5].id, favs[5].name, favs[5].service),
//            Function6 { t1, t2, t3, t4, t5, t6 -> resolving(t1, t2, t3, t4, t5, t6, favs) }
//        )
    }

//    private fun resolving(t1: LiveDataResponse, t2: LiveDataResponse, t3: LiveDataResponse, t4: LiveDataResponse, t5: LiveDataResponse, t6: LiveDataResponse, favs: List<ServiceLocation>): FavouritesResponse {
//        return FavouritesResponse(
//            mapOf(
//                favs[0] to t1.liveData,
//                favs[1] to t2.liveData,
//                favs[2] to t3.liveData,
//                favs[3] to t4.liveData,
//                favs[4] to t5.liveData,
//                favs[5] to t6.liveData
//            )
//        )
//    }

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

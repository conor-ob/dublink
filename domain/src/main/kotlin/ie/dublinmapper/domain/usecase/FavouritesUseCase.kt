package ie.dublinmapper.domain.usecase

import ie.dublinmapper.domain.model.*
import ie.dublinmapper.domain.repository.FavouriteRepository
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.location.LocationProvider
import ie.dublinmapper.permission.PermissionChecker
import ie.dublinmapper.util.LocationUtils
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.rtpi.api.Service
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class FavouritesUseCase @Inject constructor(
    private val favouriteRepository: FavouriteRepository,
    private val aircoachStopRepository: Repository<DetailedAircoachStop>,
    private val busEireannStopRepository: Repository<DetailedBusEireannStop>,
    private val irishRailStationRepository: Repository<DetailedIrishRailStation>,
    private val dublinBikesDockRepository: Repository<DetailedDublinBikesDock>,
    private val dublinBusStopRepository: Repository<DetailedDublinBusStop>,
    private val luasStopRepository: Repository<DetailedLuasStop>,
    private val permissionChecker: PermissionChecker,
    private val locationProvider: LocationProvider
) {

    fun saveFavourite(serviceLocationId: String, serviceLocationName: String, service: Service): Completable {
        val favourite = Favourite(serviceLocationId, serviceLocationName, service, 0, emptyMap())
        return favouriteRepository.saveFavourite(favourite)
    }

    fun removeFavourite(serviceLocationId: String, service: Service): Completable {
        return Completable.fromCallable {
            val favourite = getFavourite(serviceLocationId, service).blockingFirst()
            favouriteRepository.removeFavourite(favourite)
        }
    }

    private fun getFavourite(serviceLocationId: String, service: Service): Observable<Favourite> {
        return favouriteRepository.getFavourite(serviceLocationId, service)
    }

    fun getFavourites(): Observable<FavouritesResponse> {
        if (permissionChecker.isLocationPermissionGranted()) {
            return Observable.zip(
                favouriteRepository.getFavourites().map { toServiceLocations(it) },
                Observable.concat(
                    locationProvider.getLastKnownLocation(),
                    locationProvider.getLocationUpdates()
                )
                    .throttleFirst(30, TimeUnit.SECONDS),
                BiFunction { serviceLocations, coordinate ->
                    FavouritesResponse(
                        serviceLocations.sortedBy {
                            LocationUtils.haversineDistance(coordinate, it.coordinate)
                        }
                    )
                }
            )
        }
        return favouriteRepository.getFavourites()
            .map { FavouritesResponse(toServiceLocations(it)) }
    }

    private fun toServiceLocations(favourites: List<Favourite>): List<DetailedServiceLocation> {
        return favourites.map { findMatching(it) }
    }

    private fun findMatching(favourite: Favourite): DetailedServiceLocation {
        return when (favourite.service) {
            Service.AIRCOACH -> aircoachStopRepository.getById(favourite.id).blockingSingle()
            Service.BUS_EIREANN -> busEireannStopRepository.getById(favourite.id).blockingSingle()
            Service.DUBLIN_BIKES -> dublinBikesDockRepository.getById(favourite.id).blockingSingle()
            Service.DUBLIN_BUS -> dublinBusStopRepository.getById(favourite.id).blockingSingle()
            Service.IRISH_RAIL -> irishRailStationRepository.getById(favourite.id).blockingSingle()
            Service.LUAS -> luasStopRepository.getById(favourite.id).blockingSingle()
        }
    }

}

data class FavouritesResponse(
    val serviceLocations: List<DetailedServiceLocation>
)

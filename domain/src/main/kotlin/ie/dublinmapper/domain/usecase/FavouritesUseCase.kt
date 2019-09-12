package ie.dublinmapper.domain.usecase

import ie.dublinmapper.domain.model.*
import ie.dublinmapper.domain.repository.FavouriteRepository
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.util.Service
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject

class FavouritesUseCase @Inject constructor(
    private val favouriteRepository: FavouriteRepository,
    private val aircoachStopRepository: Repository<AircoachStop>,
    private val busEireannStopRepository: Repository<BusEireannStop>,
    private val irishRailStationRepository: Repository<IrishRailStation>,
    private val dublinBikesDockRepository: Repository<DublinBikesDock>,
    private val dublinBusStopRepository: Repository<DublinBusStop>,
    private val luasStopRepository: Repository<LuasStop>,
    private val swordsExpressStopRepository: Repository<SwordsExpressStop>
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
        return favouriteRepository.getFavourites()
            .map { FavouritesResponse(toServiceLocations(it)) }
    }

    private fun toServiceLocations(favourites: List<Favourite>): List<ServiceLocation> {
        return favourites.map { findMatching(it) }
    }

    private fun findMatching(favourite: Favourite): ServiceLocation {
        return when (favourite.service) {
            Service.AIRCOACH -> aircoachStopRepository.getById(favourite.id).blockingSingle()
            Service.BUS_EIREANN -> busEireannStopRepository.getById(favourite.id).blockingSingle()
            Service.DUBLIN_BIKES -> dublinBikesDockRepository.getById(favourite.id).blockingSingle()
            Service.DUBLIN_BUS -> dublinBusStopRepository.getById(favourite.id).blockingSingle()
            Service.IRISH_RAIL -> irishRailStationRepository.getById(favourite.id).blockingSingle()
            Service.LUAS -> luasStopRepository.getById(favourite.id).blockingSingle()
            Service.SWORDS_EXPRESS -> TODO()
        }
    }

}

data class FavouritesResponse(
    val serviceLocations: List<ServiceLocation>
)

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
    private val dartStationRepository: Repository<DartStation>,
    private val dublinBikesDockRepository: Repository<DublinBikesDock>,
    private val dublinBusStopRepository: Repository<DublinBusStop>,
    private val luasStopRepository: Repository<LuasStop>,
    private val swordsExpressStopRepository: Repository<SwordsExpressStop>
    ) {

    fun saveFavourite(serviceLocationId: String, serviceLocationName: String, service: Service): Completable {
        val favourite = Favourite(serviceLocationId, serviceLocationName, service, emptyMap())
        return Completable.fromCallable { favouriteRepository.saveFavourite(favourite) }
    }

    fun removeFavourite(serviceLocationId: String, serviceLocationName: String, service: Service): Completable {
        val favourite = Favourite(serviceLocationId, serviceLocationName, service, emptyMap())
        return Completable.fromCallable { favouriteRepository.removeFavourite(favourite) }
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
            Service.AIRCOACH -> TODO()
            Service.BUS_EIREANN -> TODO()
            Service.DUBLIN_BIKES -> TODO()
            Service.DUBLIN_BUS -> TODO()
            Service.IRISH_RAIL -> dartStationRepository.getById(favourite.id).blockingSingle()
            Service.LUAS -> TODO()
            Service.SWORDS_EXPRESS -> TODO()
        }
    }

}

data class FavouritesResponse(
    val serviceLocations: List<ServiceLocation>
)

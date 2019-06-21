package ie.dublinmapper.repository.favourite

import ie.dublinmapper.datamodel.favourite.FavouriteEntity
import ie.dublinmapper.datamodel.favourite.FavouriteServiceLocationCacheResource
import ie.dublinmapper.domain.model.Favourite
import ie.dublinmapper.domain.repository.FavouriteRepository
import io.reactivex.Observable
import ma.glasnost.orika.MapperFacade

class FavouriteServiceLocationRepository(
    private val cacheResource: FavouriteServiceLocationCacheResource,
    private val mapper: MapperFacade
) : FavouriteRepository {

    override fun saveFavourites(favourites: List<Favourite>) {
        cacheResource.insertFavourites(mapper.mapAsList(favourites, FavouriteEntity::class.java))
    }

    override fun removeFavourite(favourite: Favourite) {
        cacheResource.removeFavourite(mapper.map(favourite, FavouriteEntity::class.java))
    }

    override fun getFavourites(): Observable<List<Favourite>> {
        return cacheResource.selectFavourites()
            .map { entities -> mapper.mapAsList(entities, Favourite::class.java) }
            .toObservable()
//        return dao.selectAll()
//            .map { favouriteEntities ->
//                favouriteEntities.map {
//                    Favourite(
//                        id = it.location.id,
//                        name = it.location.name,
//                        service = it.location.service,
//                        routes = emptyMap() //TODO
//                    )
//                }
//            }
//            .toObservable()
//        return Observable.just(
//            listOf(
//                Favourite(
//                    id = "BROCK",
//                    name = "Blackrock Dart",
//                    service = Service.IRISH_RAIL,
//                    routes = mapOf(
//                        Operator.COMMUTER to setOf(Operator.COMMUTER.fullName),
//                        Operator.DART to setOf(Operator.DART.fullName),
//                        Operator.INTERCITY to setOf(Operator.INTERCITY.fullName)
//                    )
//                ),
//                Favourite(
//                    id = "PERSE",
//                    name = "Pearse Dart",
//                    service = Service.IRISH_RAIL,
//                    routes = mapOf(
//                        Operator.COMMUTER to setOf(Operator.COMMUTER.fullName),
//                        Operator.DART to setOf(Operator.DART.fullName),
//                        Operator.INTERCITY to setOf(Operator.INTERCITY.fullName)
//                    )
//                )
//            )
//        )
    }

}

package ie.dublinmapper.repository.favourite

import ie.dublinmapper.datamodel.favourite.FavouriteEntity
import ie.dublinmapper.datamodel.favourite.FavouriteServiceLocationCacheResource
import ie.dublinmapper.domain.model.Favourite
import ie.dublinmapper.domain.repository.FavouriteRepository
import io.reactivex.Completable
import io.reactivex.Observable
import io.rtpi.api.Service
import ma.glasnost.orika.MapperFacade

class FavouriteServiceLocationRepository(
    private val cacheResource: FavouriteServiceLocationCacheResource,
    private val mapper: MapperFacade
) : FavouriteRepository {

    override fun saveFavourite(favourite: Favourite): Completable {
        return cacheResource.countFavourites()
            .flatMapCompletable { count ->
                cacheResource.insertFavourite(mapper.map(favourite.copy(order = count), FavouriteEntity::class.java))
            }
    }

    override fun updateFavourites(favourites: List<Favourite>) {
        cacheResource.insertFavourites(mapper.mapAsList(favourites, FavouriteEntity::class.java))
    }

    override fun removeFavourite(favourite: Favourite) {
        cacheResource.removeFavourite(mapper.map(favourite, FavouriteEntity::class.java))
    }

    override fun getFavourites(): Observable<List<Favourite>> {
        return cacheResource.selectFavourites()
            .map { entities -> mapper.mapAsList(entities, Favourite::class.java) }
            .toObservable()
    }

    override fun getFavourites(service: Service): Observable<List<Favourite>> {
        return getFavourites().map { favourites -> favourites.filter { it.service == service } }
    }

    override fun getFavourite(id: String, service: Service): Observable<Favourite> {
        return cacheResource.selectFavourite(id, service)
            .map { entity -> mapper.map(entity, Favourite::class.java) }
            .toObservable()
    }

}

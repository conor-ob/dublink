package ie.dublinmapper.repository.favourite

import ie.dublinmapper.datamodel.FavouriteServiceLocationLocalResource
import ie.dublinmapper.domain.model.Favourite
import ie.dublinmapper.domain.repository.FavouriteRepository
import io.reactivex.Completable
import io.reactivex.Observable
import io.rtpi.api.Service

class FavouriteServiceLocationRepository(
    private val localResource: FavouriteServiceLocationLocalResource
) : FavouriteRepository {

    override fun saveFavourite(favourite: Favourite): Completable {
//        return localResource.countFavourites()
//            .flatMapCompletable { count ->
//                localResource.insertFavourite(mapper.map(favourite.copy(order = count), FavouriteEntity::class.java))
//            }
        TODO()
    }

    override fun updateFavourites(favourites: List<Favourite>) {
//        localResource.insertFavourites(mapper.mapAsList(favourites, FavouriteEntity::class.java))
        TODO()
    }

    override fun removeFavourite(favourite: Favourite) {
//        localResource.removeFavourite(mapper.map(favourite, FavouriteEntity::class.java))
        TODO()
    }

    override fun getFavourites(): Observable<List<Favourite>> {
//        return localResource.selectFavourites()
//            .map { entities -> mapper.mapAsList(entities, Favourite::class.java) }
        return Observable.empty()
    }

    override fun getFavourites(service: Service): Observable<List<Favourite>> {
        return getFavourites().map { favourites -> favourites.filter { it.service == service } }
    }

    override fun getFavourite(id: String, service: Service): Observable<Favourite> {
//        return localResource.selectFavourite(id, service)
//            .map { entity -> mapper.map(entity, Favourite::class.java) }
        return Observable.empty()
    }

}

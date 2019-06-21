package ie.dublinmapper.datamodel.favourite

import ie.dublinmapper.util.Service
import io.reactivex.Completable
import io.reactivex.Maybe

interface FavouriteServiceLocationCacheResource {

    fun selectFavourites(): Maybe<List<FavouriteEntity>>

    fun insertFavourite(favourite: FavouriteEntity): Completable

    fun removeFavourite(favourite: FavouriteEntity)

    fun insertFavourites(favourites: List<FavouriteEntity>)

    fun countFavourites(): Maybe<Long>

    fun selectFavourite(id: String, service: Service): Maybe<FavouriteEntity>

}

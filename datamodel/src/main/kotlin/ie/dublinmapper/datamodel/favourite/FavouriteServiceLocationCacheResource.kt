package ie.dublinmapper.datamodel.favourite

import io.reactivex.Completable
import io.reactivex.Maybe
import io.rtpi.api.Service

interface FavouriteServiceLocationCacheResource {

    fun selectFavourites(): Maybe<List<FavouriteEntity>>

    fun insertFavourite(favourite: FavouriteEntity): Completable

    fun removeFavourite(favourite: FavouriteEntity)

    fun insertFavourites(favourites: List<FavouriteEntity>)

    fun countFavourites(): Maybe<Long>

    fun selectFavourite(id: String, service: Service): Maybe<FavouriteEntity>

}

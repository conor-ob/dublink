package ie.dublinmapper.datamodel.favourite

import io.reactivex.Maybe

interface FavouriteServiceLocationCacheResource {

    fun selectFavourites(): Maybe<List<FavouriteEntity>>

    fun insertFavourite(favourite: FavouriteEntity)

    fun removeFavourite(favourite: FavouriteEntity)

    fun insertFavourites(favourites: List<FavouriteEntity>)

}

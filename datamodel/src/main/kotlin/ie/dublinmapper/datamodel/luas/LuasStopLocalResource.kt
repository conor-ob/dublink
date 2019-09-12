package ie.dublinmapper.datamodel.luas

import ie.dublinmapper.datamodel.favourite.FavouriteEntity
import io.reactivex.Maybe

interface LuasStopLocalResource {

    fun selectStops(): Maybe<List<LuasStopEntity>>

    fun selectFavouriteStops(): Maybe<List<FavouriteEntity>>

    fun insertStops(stops: List<LuasStopEntity>)

}

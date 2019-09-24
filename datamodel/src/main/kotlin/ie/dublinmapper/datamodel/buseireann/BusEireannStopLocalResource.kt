package ie.dublinmapper.datamodel.buseireann

import ie.dublinmapper.datamodel.favourite.FavouriteEntity
import io.reactivex.Maybe

interface BusEireannStopLocalResource {

    fun selectStops(): Maybe<List<BusEireannStopEntity>>

    fun selectFavouriteStops(): Maybe<List<FavouriteEntity>>

    fun insertStops(stops: List<BusEireannStopEntity>)

}

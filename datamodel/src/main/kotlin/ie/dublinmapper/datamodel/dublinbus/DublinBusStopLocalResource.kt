package ie.dublinmapper.datamodel.dublinbus

import ie.dublinmapper.datamodel.favourite.FavouriteEntity
import io.reactivex.Maybe

interface DublinBusStopLocalResource {

    fun selectStops(): Maybe<List<DublinBusStopEntity>>

    fun selectFavouriteStops(): Maybe<List<FavouriteEntity>>

    fun insertStops(stops: List<DublinBusStopEntity>)

}

package ie.dublinmapper.datamodel.aircoach

import ie.dublinmapper.datamodel.favourite.FavouriteEntity
import io.reactivex.Maybe

interface AircoachStopLocalResource {

    fun selectStops(): Maybe<List<AircoachStopEntity>>

    fun selectFavouriteStops(): Maybe<List<FavouriteEntity>>

    fun insertStops(stops: List<AircoachStopEntity>)

}

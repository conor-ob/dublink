package ie.dublinmapper.datamodel.swordsexpress

import ie.dublinmapper.datamodel.favourite.FavouriteEntity
import io.reactivex.Maybe

interface SwordsExpressStopLocalResource {

    fun selectStops(): Maybe<List<SwordsExpressStopEntity>>

    fun selectFavouriteStops(): Maybe<List<FavouriteEntity>>

    fun insertStops(stops: Pair<List<SwordsExpressStopLocationEntity>, List<SwordsExpressStopServiceEntity>>)

}

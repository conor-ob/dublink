package ie.dublinmapper.datamodel.swordsexpress

import io.reactivex.Maybe

interface SwordsExpressStopCacheResource {

    fun selectStops(): Maybe<List<SwordsExpressStopEntity>>

    fun insertStops(stops: Pair<List<SwordsExpressStopLocationEntity>, List<SwordsExpressStopServiceEntity>>)

}

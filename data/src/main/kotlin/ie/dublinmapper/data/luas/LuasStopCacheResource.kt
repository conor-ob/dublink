package ie.dublinmapper.data.luas

import io.reactivex.Maybe

interface LuasStopCacheResource {

    fun selectStops(): Maybe<List<LuasStopEntity>>

    fun insertStops(stops: Pair<List<LuasStopLocationEntity>, List<LuasStopServiceEntity>>)

}

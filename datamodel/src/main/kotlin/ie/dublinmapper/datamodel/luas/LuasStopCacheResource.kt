package ie.dublinmapper.datamodel.luas

import io.reactivex.Maybe

interface LuasStopCacheResource {

    fun selectStops(): Maybe<List<LuasStopEntity>>

    fun insertStops(stops: List<LuasStopEntity>)

}

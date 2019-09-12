package ie.dublinmapper.datamodel.dublinbus

import io.reactivex.Maybe

interface DublinBusStopCacheResource {

    fun selectStops(): Maybe<List<DublinBusStopEntity>>

    fun insertStops(stops: List<DublinBusStopEntity>)

}

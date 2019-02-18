package ie.dublinmapper.data.dublinbus

import io.reactivex.Maybe

interface DublinBusStopCacheResource {

    fun selectStops(): Maybe<List<DublinBusStopEntity>>

    fun insertStops(stops: Pair<List<DublinBusStopLocationEntity>, List<DublinBusStopServiceEntity>>)

}

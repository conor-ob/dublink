package ie.dublinmapper.datamodel.buseireann

import io.reactivex.Maybe

interface BusEireannStopCacheResource {

    fun selectStops(): Maybe<List<BusEireannStopEntity>>

    fun insertStops(stops: List<BusEireannStopEntity>)

}

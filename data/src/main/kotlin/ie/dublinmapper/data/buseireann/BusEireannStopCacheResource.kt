package ie.dublinmapper.data.buseireann

import io.reactivex.Maybe

interface BusEireannStopCacheResource {

    fun selectStops(): Maybe<List<BusEireannStopEntity>>

    fun insertStops(stops: List<BusEireannStopEntity>)

}

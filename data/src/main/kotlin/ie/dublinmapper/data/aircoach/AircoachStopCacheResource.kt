package ie.dublinmapper.data.aircoach

import io.reactivex.Maybe

interface AircoachStopCacheResource {

    fun selectStops(): Maybe<List<AircoachStopEntity>>

    fun insertStops(stops: List<AircoachStopEntity>)

}

package ie.dublinmapper.datamodel.aircoach

import io.reactivex.Maybe

interface AircoachStopCacheResource {

    fun selectStops(): Maybe<List<AircoachStopEntity>>

    fun insertStops(stops: List<AircoachStopEntity>)

}

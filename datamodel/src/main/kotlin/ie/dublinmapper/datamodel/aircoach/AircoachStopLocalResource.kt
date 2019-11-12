package ie.dublinmapper.datamodel.aircoach

import io.reactivex.Observable
import io.rtpi.api.AircoachStop

interface AircoachStopLocalResource {

    fun selectStops(): Observable<List<AircoachStop>>

    fun insertStops(stops: List<AircoachStop>)

}

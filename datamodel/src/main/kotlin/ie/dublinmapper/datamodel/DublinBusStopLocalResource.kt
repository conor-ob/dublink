package ie.dublinmapper.datamodel

import io.reactivex.Observable
import io.rtpi.api.DublinBusStop

interface DublinBusStopLocalResource {

    fun selectStops(): Observable<List<DublinBusStop>>

    fun insertStops(stops: List<DublinBusStop>)

}

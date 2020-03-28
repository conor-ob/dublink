package ie.dublinmapper.domain.datamodel

import io.reactivex.Observable
import io.rtpi.api.BusEireannStop

interface BusEireannStopLocalResource {

    fun selectStops(): Observable<List<BusEireannStop>>

    fun insertStops(stops: List<BusEireannStop>)
}

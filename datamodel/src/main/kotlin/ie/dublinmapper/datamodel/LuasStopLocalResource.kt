package ie.dublinmapper.datamodel

import io.reactivex.Observable
import io.rtpi.api.LuasStop

interface LuasStopLocalResource {

    fun selectStops(): Observable<List<LuasStop>>

    fun insertStops(stops: List<LuasStop>)

}

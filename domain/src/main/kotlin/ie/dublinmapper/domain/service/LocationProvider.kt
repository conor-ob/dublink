package ie.dublinmapper.domain.service

import io.reactivex.Observable
import io.rtpi.api.Coordinate

interface LocationProvider {

    fun getLastKnownLocation(): Observable<Coordinate>

    fun getLocationUpdates(): Observable<Coordinate>
}

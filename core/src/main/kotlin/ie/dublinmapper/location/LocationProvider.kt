package ie.dublinmapper.location

import io.reactivex.Observable
import io.reactivex.Single
import io.rtpi.api.Coordinate

interface LocationProvider {

    fun getLastKnownLocation(): Single<Coordinate>

    fun getLocationUpdates(): Observable<Coordinate>

}

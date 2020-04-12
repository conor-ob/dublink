package ie.dublinmapper.domain.service

import io.reactivex.Observable
import io.rtpi.api.Coordinate

interface LocationProvider {

    /**
     * Returns a stream of location update events
     * @param thresholdDistance the threshold distance in metres. The stream will only emit a new
     * location if the distance between the new location and the previous location is greater than
     * the threshold distance
     */
    fun getLocationUpdates(thresholdDistance: Double): Observable<Coordinate>
}

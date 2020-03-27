package ie.dublinmapper.location

import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationRequest
import ie.dublinmapper.core.LocationProvider
import io.reactivex.Observable
import io.rtpi.api.Coordinate
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider
import javax.inject.Inject

class GpsLocationProvider @Inject constructor(context: Context) : LocationProvider {

    private val locationProvider = ReactiveLocationProvider(context)
    private var lastKnownLocation: Location? = null

    override fun getLastKnownLocation(): Observable<Coordinate> {
        return locationProvider.lastKnownLocation
                .filter { isBetterLocation(it) }
                .doOnNext { lastKnownLocation = it }
                .map { Coordinate(it.latitude, it.longitude) }
    }

    override fun getLocationUpdates(): Observable<Coordinate> {
        return locationProvider.getUpdatedLocation(newRequest())
            .filter { isBetterLocation(it) }
            .doOnNext { lastKnownLocation = it }
            .map { Coordinate(it.latitude, it.longitude) }
    }

    private fun newRequest(): LocationRequest {
        return LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(500L)
    }

    private fun isBetterLocation(location: Location): Boolean {
        if (lastKnownLocation == null) {
            // A new location is always better than no location
            return true
        }

        // Check whether the new location fix is newer or older
        val timeDelta = location.time - lastKnownLocation!!.time
        val twoMinutes = 1000 * 60 * 2
        val isSignificantlyNewer = timeDelta > twoMinutes
        val isSignificantlyOlder = timeDelta < -twoMinutes
        val isNewer = timeDelta > 0

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false
        }

        // Check whether the new location fix is more or less accurate
        val accuracyDelta = (location.accuracy - lastKnownLocation!!.accuracy).toInt()
        val isLessAccurate = accuracyDelta > 0
        val isMoreAccurate = accuracyDelta < 0
        val isSignificantlyLessAccurate = accuracyDelta > 200

        // Check if the old and new location are from the same provider
        val isFromSameProvider = isSameProvider(location.provider, lastKnownLocation!!.provider)

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true
        } else if (isNewer && !isLessAccurate) {
            return true
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true
        }
        return false
    }

    /** Checks whether two providers are the same  */
    private fun isSameProvider(provider1: String?, provider2: String?): Boolean {
        return if (provider1 == null) {
            provider2 == null
        } else provider1 == provider2
    }
}

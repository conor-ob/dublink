package ie.dublinmapper.location

import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationRequest
import ie.dublinmapper.domain.service.LocationProvider
import ie.dublinmapper.domain.service.PreferenceStore
import ie.dublinmapper.domain.util.haversine
import io.reactivex.Observable
import io.rtpi.api.Coordinate
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider
import timber.log.Timber
import javax.inject.Inject

class GpsLocationProvider @Inject constructor(
    private val preferenceStore: PreferenceStore,
    context: Context
) : LocationProvider {

    private var lastKnownLocation: Location? = null

    private val locationProvider = ReactiveLocationProvider(context)

    override fun getLocationUpdates(thresholdDistance: Double): Observable<Coordinate> {
        return Observable.concat(
            Observable.just(preferenceStore.getLastKnownLocation()),
            getLastKnownLocation(),
            getLocationUpdates()
        )
            .distinctUntilChanged { c1, c2 -> c1.haversine(c2) <= thresholdDistance }
    }

    private fun getLastKnownLocation(): Observable<Coordinate> {
        return locationProvider.lastKnownLocation
                .filter { isBetterLocation(it) }
                .doOnNext { saveLocation(it) }
                .map { Coordinate(it.latitude, it.longitude) }
    }

    private fun saveLocation(location: Location) {
        lastKnownLocation = location
        preferenceStore.setLastKnownLocation(Coordinate(location.latitude, location.longitude))
    }

    private fun getLocationUpdates(): Observable<Coordinate> {
        return locationProvider.getUpdatedLocation(newRequest())
            .filter { isBetterLocation(it) }
            .doOnNext { saveLocation(it) }
            .map { Coordinate(it.latitude, it.longitude) }
    }

    private fun newRequest() = LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        fastestInterval = 5000L
        interval = 10000L
    }

    private fun isBetterLocation(location: Location): Boolean {
        Timber.d(location.toString())
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

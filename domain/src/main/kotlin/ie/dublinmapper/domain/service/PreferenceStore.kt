package ie.dublinmapper.domain.service

import io.rtpi.api.Coordinate
import io.rtpi.api.Service

interface PreferenceStore {

    fun containsPreferredTheme(): Boolean

    fun getPreferredTheme(): String?

    fun setPreferredTheme(preferredTheme: String): Boolean

    fun isFavouritesSortByLocation(): Boolean

    fun getFavouritesLiveDataLimit(): Int

    fun getLiveDataRefreshInterval(): Long

    fun isServiceEnabled(service: Service): Boolean

    fun setServiceEnabled(service: Service): Boolean

    fun getLastKnownLocation(): Coordinate

    fun setLastKnownLocation(coordinate: Coordinate)
}

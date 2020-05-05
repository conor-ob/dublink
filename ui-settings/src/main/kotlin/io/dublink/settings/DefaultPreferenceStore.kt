package io.dublink.settings

import android.content.Context
import androidx.preference.PreferenceManager
import io.dublink.domain.service.PreferenceStore
import io.rtpi.api.Coordinate
import io.rtpi.api.Service
import javax.inject.Inject

class DefaultPreferenceStore @Inject constructor(
    private val context: Context
) : PreferenceStore {

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    override fun containsPreferredTheme() =
        preferences.contains(context.resources.getString(R.string.preference_key_preferred_theme))

    override fun getPreferredTheme() =
        preferences.getString(
            context.resources.getString(R.string.preference_key_preferred_theme),
            context.resources.getString(R.string.preference_default_preferred_theme)
        )

    override fun setPreferredTheme(preferredTheme: String) =
        preferences.edit().putString(
            context.resources.getString(R.string.preference_key_preferred_theme),
            preferredTheme
        ).commit()

    override fun isFavouritesSortByLocation() =
        preferences.getBoolean(
            context.resources.getString(R.string.preference_key_favourites_sort_by_location),
            context.resources.getBoolean(R.bool.preference_default_favourites_sort_location)
        )

    override fun getFavouritesLiveDataLimit() =
        preferences.getInt(
            context.resources.getString(R.string.preference_key_favourites_live_data_limit),
            context.resources.getInteger(R.integer.preference_default_favourites_live_data_limit)
        )

    override fun getLiveDataRefreshInterval() =
        preferences.getInt(
            context.resources.getString(R.string.preference_key_live_data_refresh_interval),
            context.resources.getInteger(R.integer.preference_default_live_data_refresh_interval)
        ).toLong()

    override fun getLastKnownLocation(): Coordinate {
        return Coordinate(
            preferences.getFloat(
                context.resources.getString(R.string.preference_key_last_known_location_latitude),
                53.347274f // Dublin latitude
            ).toDouble(),
            preferences.getFloat(
                context.resources.getString(R.string.preference_key_last_known_location_longitude),
                -6.259159f // Dublin longitude
            ).toDouble()
        )
    }

    override fun setLastKnownLocation(coordinate: Coordinate) {
        preferences.edit().putFloat(
            context.resources.getString(R.string.preference_key_last_known_location_latitude),
            coordinate.latitude.toFloat()
        ).apply()
        preferences.edit().putFloat(
            context.resources.getString(R.string.preference_key_last_known_location_longitude),
            coordinate.longitude.toFloat()
        ).apply()
    }

    override fun isServiceEnabled(service: Service): Boolean {
        val key = when (service) {
            Service.AIRCOACH ->
                context.resources.getString(R.string.preference_key_enabled_service_aircoach)
            Service.BUS_EIREANN ->
                context.resources.getString(R.string.preference_key_enabled_service_bus_eireann)
            Service.DUBLIN_BIKES ->
                context.resources.getString(R.string.preference_key_enabled_service_dublin_bikes)
            Service.DUBLIN_BUS ->
                context.resources.getString(R.string.preference_key_enabled_service_dublin_bus)
            Service.IRISH_RAIL ->
                context.resources.getString(R.string.preference_key_enabled_service_irish_rail)
            Service.LUAS ->
                context.resources.getString(R.string.preference_key_enabled_service_luas)
        }
        val default = when (service) {
            Service.AIRCOACH ->
                context.resources.getBoolean(R.bool.preference_default_enabled_service_aircoach)
            Service.BUS_EIREANN ->
                context.resources.getBoolean(R.bool.preference_default_enabled_service_bus_eireann)
            Service.DUBLIN_BIKES ->
                context.resources.getBoolean(R.bool.preference_default_enabled_service_dublin_bikes)
            Service.DUBLIN_BUS ->
                context.resources.getBoolean(R.bool.preference_default_enabled_service_dublin_bus)
            Service.IRISH_RAIL ->
                context.resources.getBoolean(R.bool.preference_default_enabled_service_irish_rail)
            Service.LUAS ->
                context.resources.getBoolean(R.bool.preference_default_enabled_service_luas)
        }
        return preferences.getBoolean(key, default)
    }

    override fun setServiceEnabled(service: Service): Boolean {
        val key = when (service) {
            Service.AIRCOACH ->
                context.resources.getString(R.string.preference_key_enabled_service_aircoach)
            Service.BUS_EIREANN ->
                context.resources.getString(R.string.preference_key_enabled_service_bus_eireann)
            Service.DUBLIN_BIKES ->
                context.resources.getString(R.string.preference_key_enabled_service_dublin_bikes)
            Service.DUBLIN_BUS ->
                context.resources.getString(R.string.preference_key_enabled_service_dublin_bus)
            Service.IRISH_RAIL ->
                context.resources.getString(R.string.preference_key_enabled_service_irish_rail)
            Service.LUAS ->
                context.resources.getString(R.string.preference_key_enabled_service_luas)
        }
        return preferences.edit().putBoolean(key, true).commit()
    }

    override fun isShowNearbyPlacesEnabled(): Boolean {
        return preferences.getBoolean(
            context.resources.getString(R.string.preference_key_search_show_nearby),
            context.resources.getBoolean(R.bool.preference_default_search_show_nearby)
        )
    }

    override fun isShowRecentSearchesEnabled(): Boolean {
        return preferences.getBoolean(
            context.resources.getString(R.string.preference_key_search_show_recents),
            context.resources.getBoolean(R.bool.preference_default_search_show_recents)
        )
    }
}

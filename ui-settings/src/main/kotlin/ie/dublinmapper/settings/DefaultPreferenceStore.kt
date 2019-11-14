package ie.dublinmapper.settings

import android.content.Context
import androidx.preference.PreferenceManager
import ie.dublinmapper.util.PreferenceStore
import javax.inject.Inject

class DefaultPreferenceStore @Inject constructor(context: Context) : PreferenceStore {

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    override fun getString(preferenceKey: String, defaultValue: String): String {
        return preferences.getString(preferenceKey, defaultValue) ?: defaultValue
    }

    override fun setString(preferenceKey: String, value: String): Boolean {
        return preferences.edit().putString(preferenceKey, value).commit()
    }

    override fun getInt(preferenceKey: String, defaultValue: Int): Int {
        return preferences.getInt(preferenceKey, defaultValue)
    }

    override fun setInt(preferenceKey: String, value: Int): Boolean {
        return preferences.edit().putInt(preferenceKey, value).commit()
    }

    override fun getBoolean(preferenceKey: String, defaultValue: Boolean): Boolean {
        return preferences.getBoolean(preferenceKey, defaultValue)
    }

    override fun setBoolean(preferenceKey: String, value: Boolean): Boolean {
        return preferences.edit().putBoolean(preferenceKey, value).commit()
    }

    override fun contains(preferenceKey: String): Boolean {
        return preferences.contains(preferenceKey)
    }

}

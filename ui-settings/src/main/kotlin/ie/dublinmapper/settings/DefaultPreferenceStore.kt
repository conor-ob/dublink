package ie.dublinmapper.settings

import android.content.Context
import androidx.preference.PreferenceManager
import ie.dublinmapper.util.PreferenceStore
import javax.inject.Inject

class DefaultPreferenceStore @Inject constructor(context: Context) : PreferenceStore {

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    override fun getBoolean(preferenceKey: String, defaultValue: Boolean): Boolean {
        return preferences.getBoolean(preferenceKey, defaultValue)
    }

    override fun setBoolean(preferenceKey: String, value: Boolean): Boolean {
        return preferences.edit().putBoolean(preferenceKey, value).commit()
    }

}

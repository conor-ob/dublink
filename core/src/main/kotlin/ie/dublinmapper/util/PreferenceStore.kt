package ie.dublinmapper.util

interface PreferenceStore {

    fun getBoolean(preferenceKey: String, defaultValue: Boolean): Boolean

    fun setBoolean(preferenceKey: String, value: Boolean): Boolean

}

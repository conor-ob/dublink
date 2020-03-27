package ie.dublinmapper.core

interface PreferenceStore {

    fun getString(preferenceKey: String, defaultValue: String): String

    fun setString(preferenceKey: String, value: String): Boolean

    fun getInt(preferenceKey: String, defaultValue: Int): Int

    fun setInt(preferenceKey: String, value: Int): Boolean

    fun getBoolean(preferenceKey: String, defaultValue: Boolean): Boolean

    fun setBoolean(preferenceKey: String, value: Boolean): Boolean

    fun contains(preferenceKey: String): Boolean
}

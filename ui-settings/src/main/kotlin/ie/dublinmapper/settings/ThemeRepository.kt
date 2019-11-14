package ie.dublinmapper.settings

import android.content.res.Resources
import android.os.Build
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import ie.dublinmapper.util.PreferenceStore
import javax.inject.Inject

class ThemeRepository @Inject constructor(
    private val resources: Resources,
    private val preferenceStore: PreferenceStore
) {

    fun setPreferredThemeOrDefault() {
        val themeMode = getPreferredThemeOrDefault().mode
        AppCompatDelegate.setDefaultNightMode(themeMode)
    }

    fun setTheme(name: String) {
        val defaultTheme = getDefaultTheme()
        val theme = getThemes().find { theme -> resources.getString(theme.nameId) == name } ?: defaultTheme
        AppCompatDelegate.setDefaultNightMode(theme.mode)
    }

    private fun getPreferredThemeOrDefault(): Theme {
        val defaultTheme = getDefaultTheme()
        val preferredThemeOrDefault = preferenceStore.getString(
            preferenceKey = resources.getString(R.string.preference_preferred_theme),
            defaultValue = resources.getString(defaultTheme.nameId)
        )
        return getThemes().find { theme -> resources.getString(theme.nameId) == preferredThemeOrDefault } ?: defaultTheme
    }

    private fun getDefaultTheme(): Theme {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Theme.SYSTEM_DEFAULT
        } else {
            Theme.BATTERY_SAVER
        }
    }

    private fun getThemes(): List<Theme> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            listOf(Theme.LIGHT, Theme.DARK, Theme.SYSTEM_DEFAULT)
        } else {
            listOf(Theme.LIGHT, Theme.DARK, Theme.BATTERY_SAVER)
        }
    }

}

enum class Theme(val mode: Int, @StringRes val nameId: Int) {

    LIGHT(AppCompatDelegate.MODE_NIGHT_NO, R.string.theme_light),
    DARK(AppCompatDelegate.MODE_NIGHT_YES, R.string.theme_dark),
    SYSTEM_DEFAULT(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, R.string.theme_system_default),
    BATTERY_SAVER(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY, R.string.theme_battery_saver)

}

package io.dublink.settings

import android.content.res.Resources
import android.os.Build
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import io.dublink.domain.service.PreferenceStore
import javax.inject.Inject

class ThemeRepository @Inject constructor(
    private val resources: Resources,
    private val preferenceStore: PreferenceStore
) {

    fun setPreferredThemeOrDefault() {
        val theme = getPreferredThemeOrDefault()
        if (!preferenceStore.containsPreferredTheme()) {
            preferenceStore.setPreferredTheme(resources.getString(theme.value))
        }
        AppCompatDelegate.setDefaultNightMode(theme.mode)
    }

    fun setTheme(name: String) {
        val defaultTheme = getDefaultTheme()
        val theme = getThemes().find { theme ->
            resources.getString(theme.value) == name } ?: defaultTheme
        AppCompatDelegate.setDefaultNightMode(theme.mode)
    }

    private fun getPreferredThemeOrDefault(): Theme {
        val defaultTheme = getDefaultTheme()
        val preferredThemeOrDefault = preferenceStore.getPreferredTheme()
        return getThemes().find { theme ->
            resources.getString(theme.value) == preferredThemeOrDefault } ?: defaultTheme
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
            listOf(
                Theme.LIGHT,
                Theme.DARK,
                Theme.SYSTEM_DEFAULT
            )
        } else {
            listOf(
                Theme.LIGHT,
                Theme.DARK,
                Theme.BATTERY_SAVER
            )
        }
    }
}

enum class Theme(val mode: Int, @StringRes val value: Int) {

    LIGHT(AppCompatDelegate.MODE_NIGHT_NO,
        R.string.preference_value_light_theme
    ),
    DARK(AppCompatDelegate.MODE_NIGHT_YES,
        R.string.preference_value_dark_theme
    ),
    SYSTEM_DEFAULT(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
        R.string.preference_value_system_default_theme
    ),
    BATTERY_SAVER(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY,
        R.string.preference_value_battery_saver_theme
    )
}

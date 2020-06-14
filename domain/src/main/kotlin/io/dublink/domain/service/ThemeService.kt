package io.dublink.domain.service

interface ThemeService {
    fun setDefaultTheme()
    fun setPreferredThemeOrDefault()
    fun setTheme(name: String)
    fun setLightTheme()
    fun setDarkTheme()
}

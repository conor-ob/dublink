package io.dublink.domain.service

interface ThemeService {
    fun setPreferredThemeOrDefault()
    fun setTheme(name: String)
    fun setDarkTheme()
}

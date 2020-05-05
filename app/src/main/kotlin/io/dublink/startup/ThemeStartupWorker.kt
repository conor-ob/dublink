package io.dublink.startup

import android.app.Application
import io.dublink.settings.ThemeRepository

class ThemeStartupWorker(private val themeRepository: ThemeRepository) : StartupWorker {

    override fun startup(application: Application) {
        themeRepository.setPreferredThemeOrDefault()
    }
}

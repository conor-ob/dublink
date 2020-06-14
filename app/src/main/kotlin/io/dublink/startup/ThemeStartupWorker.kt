package io.dublink.startup

import android.app.Application
import io.dublink.domain.service.ThemeService

class ThemeStartupWorker(private val themeService: ThemeService) : StartupWorker {

    override fun startup(application: Application) {
        themeService.setPreferredThemeOrDefault()
    }
}

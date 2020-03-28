package ie.dublinmapper.startup

import android.app.Application
import ie.dublinmapper.settings.ThemeRepository

class ThemeStartupWorker(private val themeRepository: ThemeRepository) : StartupWorker {

    override fun startup(application: Application) {
        themeRepository.setPreferredThemeOrDefault()
    }
}

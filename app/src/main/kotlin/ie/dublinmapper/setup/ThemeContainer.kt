package ie.dublinmapper.setup

import android.app.Application
import ie.dublinmapper.settings.ThemeRepository

class ThemeContainer(private val themeRepository: ThemeRepository) : SetupContainer {

    override fun setup(application: Application) {
        themeRepository.setPreferredThemeOrDefault()
    }
}

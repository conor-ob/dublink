package ie.dublinmapper.init

import android.app.Application
import ie.dublinmapper.settings.ThemeRepository

class ThemeInitializer(private val themeRepository: ThemeRepository) : ApplicationInitializer {

    override fun initialize(application: Application) {
        themeRepository.setPreferredThemeOrDefault()
    }

}

package io.dublink.settings

import android.content.res.Resources
import io.dublink.domain.service.DubLinkProService
import io.dublink.domain.service.PreferenceStore
import io.dublink.domain.service.ThemeService

class DubLinkProPreferencesService(
    private val preferenceStore: PreferenceStore,
    private val themeService: ThemeService,
    private val resources: Resources
) : DubLinkProService {

    private var trial = false

    override fun isFreeTrialRunning() = trial

    override fun grantDubLinkProAccess() {
        preferenceStore.setDubLinkProEnabled(true)
    }

    override fun grantDubLinkProTrial() {
        trial = true
        grantDubLinkProPreferences()
        preferenceStore.setPreferredTheme(resources.getString(R.string.preference_value_dark_theme))
        themeService.setDarkTheme()
    }

    override fun grantDubLinkProPreferences() {
        grantDubLinkProAccess()
        preferenceStore.setFavouritesLiveDataLimit(resources.getInteger(R.integer.preference_default_favourites_live_data_limit_pro))
    }

    override fun revokeDubLinkProPreferences() {
        if (!trial) {
            preferenceStore.setDubLinkProEnabled(false)
            preferenceStore.setFavouritesSortByLocation(resources.getBoolean(R.bool.preference_default_favourites_sort_location))
            preferenceStore.setFavouritesLiveDataLimit(resources.getInteger(R.integer.preference_default_favourites_live_data_limit))
            preferenceStore.setPreferredTheme(resources.getString(R.string.preference_value_light_theme))
            themeService.setTheme(resources.getString(R.string.preference_value_light_theme))
        }
    }

    override fun revokeDubLinkProTrial() {
        trial = false
    }
}

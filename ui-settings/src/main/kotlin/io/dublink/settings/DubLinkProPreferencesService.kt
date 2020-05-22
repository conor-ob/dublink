package io.dublink.settings

import android.content.res.Resources
import io.dublink.domain.service.DubLinkProService
import io.dublink.domain.service.PreferenceStore

class DubLinkProPreferencesService(
    private val preferenceStore: PreferenceStore,
    private val themeRepository: ThemeRepository,
    private val resources: Resources
) : DubLinkProService {

    override fun grantDubLinkProPreferences() {
        preferenceStore.setDubLinkProEnabled(true)
    }

    override fun revokeDubLinkProPreferences() {
        preferenceStore.setDubLinkProEnabled(false)
        preferenceStore.setFavouritesSortByLocation(resources.getBoolean(R.bool.preference_default_favourites_sort_location))
        preferenceStore.setFavouritesLiveDataLimit(resources.getInteger(R.integer.preference_default_favourites_live_data_limit))
        themeRepository.setTheme(resources.getString(R.string.preference_value_light_theme))
        themeRepository.setPreferredThemeOrDefault()
    }
}

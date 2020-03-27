package ie.dublinmapper.setup

import android.app.Application
import androidx.preference.PreferenceManager
import ie.dublinmapper.R

class PreferencesContainer : SetupContainer {

    override fun setup(application: Application) {
        PreferenceManager.setDefaultValues(application, R.xml.preferences, false)
    }
}

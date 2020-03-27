package ie.dublinmapper.init

import android.app.Application
import androidx.preference.PreferenceManager
import ie.dublinmapper.R

class PreferencesInitializer : ApplicationInitializer {

    override fun initialize(application: Application) {
        PreferenceManager.setDefaultValues(application, R.xml.preferences, false)
    }
}

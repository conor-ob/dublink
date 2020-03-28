package ie.dublinmapper.startup

import android.app.Application
import androidx.preference.PreferenceManager
import ie.dublinmapper.R

class PreferencesStartupWorker : StartupWorker {

    override fun startup(application: Application) {
        PreferenceManager.setDefaultValues(application, R.xml.preferences, false)
    }
}

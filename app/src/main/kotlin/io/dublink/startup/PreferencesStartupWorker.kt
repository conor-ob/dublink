package io.dublink.startup

import android.app.Application
import androidx.preference.PreferenceManager
import io.dublink.R

class PreferencesStartupWorker : StartupWorker {

    override fun startup(application: Application) {
        PreferenceManager.setDefaultValues(application, R.xml.preferences, false)
    }
}

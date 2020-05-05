package io.dublink.startup

import android.app.Application
import com.facebook.stetho.Stetho
import io.dublink.BuildConfig

class StethoStartupWorker : StartupWorker {

    override fun startup(application: Application) {
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(application)
        }
    }
}

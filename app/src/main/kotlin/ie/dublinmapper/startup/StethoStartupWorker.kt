package ie.dublinmapper.startup

import android.app.Application
import com.facebook.stetho.Stetho
import ie.dublinmapper.BuildConfig

class StethoStartupWorker : StartupWorker {

    override fun startup(application: Application) {
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(application)
        }
    }
}

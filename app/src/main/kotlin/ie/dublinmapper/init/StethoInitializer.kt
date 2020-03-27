package ie.dublinmapper.init

import android.app.Application
import com.facebook.stetho.Stetho
import ie.dublinmapper.BuildConfig

class StethoInitializer : ApplicationInitializer {

    override fun initialize(application: Application) {
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(application)
        }
    }
}

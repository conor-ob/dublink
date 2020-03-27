package ie.dublinmapper.setup

import android.app.Application
import com.facebook.stetho.Stetho
import ie.dublinmapper.BuildConfig

class StethoContainer : SetupContainer {

    override fun setup(application: Application) {
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(application)
        }
    }
}

package ie.dublinmapper.setup

import android.app.Application
import com.ww.roxie.Roxie
import ie.dublinmapper.BuildConfig
import timber.log.Timber
import javax.inject.Inject

class TimberContainer @Inject constructor() : SetupContainer {

    override fun setup(application: Application) {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        Roxie.enableLogging(
            object : Roxie.Logger {
                override fun log(msg: String) {
                    Timber.tag("Roxie").d(msg)
                }
            }
        )
    }
}

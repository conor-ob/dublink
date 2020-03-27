package ie.dublinmapper.init

import android.app.Application
import com.ww.roxie.Roxie
import ie.dublinmapper.BuildConfig
import timber.log.Timber
import javax.inject.Inject

class TimberInitializer @Inject constructor() : ApplicationInitializer {

    override fun initialize(application: Application) {
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

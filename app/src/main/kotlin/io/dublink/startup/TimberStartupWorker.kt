package io.dublink.startup

import android.app.Application
import com.ww.roxie.Roxie
import io.dublink.BuildConfig
import javax.inject.Inject
import timber.log.Timber

class TimberStartupWorker @Inject constructor() : StartupWorker {

    override fun startup(application: Application) {
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

package io.dublink.startup

import android.app.Application
import com.ww.roxie.Roxie
import io.dublink.BuildConfig
import io.dublink.logging.DebugLogger
import javax.inject.Inject
import timber.log.Timber
import com.nodesagency.logviewer.Logger

class LoggingStartupWorker @Inject constructor() : StartupWorker {

    override fun startup(application: Application) {
        if (BuildConfig.DEBUG) {
            Logger.initialize(application)
            Roxie.enableLogging(
                object : Roxie.Logger {
                    override fun log(msg: String) {
                        Timber.d(msg)
                    }
                }
            )
            Timber.plant(DebugLogger)
        }
    }
}

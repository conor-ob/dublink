package io.dublink.startup

import android.app.Application
import com.nodesagency.logviewer.Logger
import com.ww.roxie.Roxie
import io.dublink.BuildConfig
import io.dublink.logging.DebugLogger
import io.dublink.logging.FirebaseCrashlyticsTree
import javax.inject.Inject
import timber.log.Timber

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
        } else {
            Timber.plant(FirebaseCrashlyticsTree)
        }
    }
}

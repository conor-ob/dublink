package io.dublink.startup

import android.app.Application
import com.ww.roxie.Roxie
import io.dublink.BuildConfig
import io.dublink.logging.FirebaseCrashlyticsTree
import javax.inject.Inject
import timber.log.Timber

class LoggingStartupWorker @Inject constructor() : StartupWorker {

    override fun startup(application: Application) {
        if (BuildConfig.DEBUG) {
            Roxie.enableLogging(
                object : Roxie.Logger {
                    override fun log(msg: String) {
                        Timber.d(msg)
                    }
                }
            )
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(FirebaseCrashlyticsTree)
        }
    }
}

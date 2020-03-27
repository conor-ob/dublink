package ie.dublinmapper.setup

import android.app.Application
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import timber.log.Timber
import java.io.IOException
import java.net.SocketException

class RxContainer : SetupContainer {

    override fun setup(application: Application) {
        RxJavaPlugins.setErrorHandler {
            var e = it
            if (e is UndeliverableException) {
                e = e.cause
            }

            if (e is IOException || e is SocketException) {
                //
            } else if (e is InterruptedException) {
                //
            } else if (e is NullPointerException || e is IllegalArgumentException) {
                //
            } else if (e is IllegalStateException) {
                //
                Thread.currentThread().uncaughtExceptionHandler
                    .uncaughtException(Thread.currentThread(), e)
            } else {
                Timber.w(e)
            }
        }
    }
}

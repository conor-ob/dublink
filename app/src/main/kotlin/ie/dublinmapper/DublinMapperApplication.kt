package ie.dublinmapper

import com.facebook.stetho.Stetho
import com.jakewharton.threetenabp.AndroidThreeTen
import com.ww.roxie.Roxie
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import ie.dublinmapper.di.DaggerApplicationComponent
import ie.dublinmapper.settings.ThemeRepository
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import timber.log.Timber
import java.io.IOException
import java.net.SocketException
import javax.inject.Inject

class DublinMapperApplication : DaggerApplication() {

    @Inject
    lateinit var themeRepository: ThemeRepository

    init {
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

    override fun onCreate() {
        super.onCreate()
        setupTimber()
        setupThreeTen()
        setupTheme()
        setupStetho()
    }

    private fun setupTheme() {
        themeRepository.setPreferredThemeOrDefault()
    }

    private fun setupTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        Roxie.enableLogging(object : Roxie.Logger {
            override fun log(msg: String) {
                Timber.tag("Roxie").d(msg)
            }
        })
    }

    private fun setupThreeTen() {
        AndroidThreeTen.init(applicationContext)
    }

    private fun setupStetho() {
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.factory().create(this)
    }

}

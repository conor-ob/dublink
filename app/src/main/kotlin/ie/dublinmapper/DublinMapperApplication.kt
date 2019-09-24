package ie.dublinmapper

import com.jakewharton.threetenabp.AndroidThreeTen
import com.ww.roxie.Roxie
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import ie.dublinmapper.di.DaggerApplicationComponent
import timber.log.Timber

class DublinMapperApplication : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()
        setupTimber()
        setupThreeTen()
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

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.factory().create(this)
    }

}

package ie.dublinmapper

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import ie.dublinmapper.database.BuildConfig
import ie.dublinmapper.di.ApplicationComponent
import ie.dublinmapper.di.ApplicationModule
import ie.dublinmapper.di.DaggerApplicationComponent
import timber.log.Timber

class DublinMapperApplication : Application() {

    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        setupDagger()
        setupTimber()
        setupThreeTen()
    }

    private fun setupDagger() {
        applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
    }

    private fun setupTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun setupThreeTen() {
        AndroidThreeTen.init(applicationContext)
    }

}

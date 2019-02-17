package ie.dublinmapper

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import ie.dublinmapper.di.ApplicationComponent
import ie.dublinmapper.di.ApplicationModule
import ie.dublinmapper.di.DaggerBuildVariantApplicationComponent
import timber.log.Timber

class DublinMapperApplication : Application() {

    lateinit var applicationComponent: ApplicationComponent

    //TODO inject app initializers
    override fun onCreate() {
        super.onCreate()
        setupDagger()
        setupTimber()
        AndroidThreeTen.init(applicationContext)
    }

    private fun setupDagger() {
        applicationComponent = DaggerBuildVariantApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
    }

    private fun setupTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

}

package ie.dublinmapper

import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import ie.dublinmapper.di.DaggerApplicationComponent
import timber.log.Timber

class DublinMapperApplication : DaggerApplication() {

    //TODO inject app initializers
    override fun onCreate() {
        super.onCreate()
        setupTimber()
        AndroidThreeTen.init(applicationContext)
    }

    private fun setupTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.builder().create(this)
    }

}

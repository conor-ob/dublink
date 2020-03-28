package ie.dublinmapper

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import ie.dublinmapper.inject.DaggerApplicationComponent
import ie.dublinmapper.startup.StartupWorkers
import javax.inject.Inject

class DublinMapperApplication : DaggerApplication() {

    @Inject lateinit var workers: StartupWorkers

    override fun onCreate() {
        super.onCreate()
        workers.startup(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.factory().create(this)
    }
}

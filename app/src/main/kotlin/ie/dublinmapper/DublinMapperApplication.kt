package ie.dublinmapper

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import ie.dublinmapper.inject.DaggerApplicationComponent
import ie.dublinmapper.setup.SetupContainers
import javax.inject.Inject

class DublinMapperApplication : DaggerApplication() {

    @Inject lateinit var containers: SetupContainers

    override fun onCreate() {
        super.onCreate()
        containers.setup(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.factory().create(this)
    }
}

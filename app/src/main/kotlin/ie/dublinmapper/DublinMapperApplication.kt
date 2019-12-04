package ie.dublinmapper

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import ie.dublinmapper.di.DaggerApplicationComponent
import ie.dublinmapper.init.ApplicationInitializers
import javax.inject.Inject

class DublinMapperApplication : DaggerApplication() {

    @Inject
    lateinit var initializers: ApplicationInitializers

    override fun onCreate() {
        super.onCreate()
        initializers.initialize(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.factory().create(this)
    }

}

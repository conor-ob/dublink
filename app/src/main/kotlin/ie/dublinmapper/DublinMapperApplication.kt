package ie.dublinmapper

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import ie.dublinmapper.di.DaggerApplicationComponent

class DublinMapperApplication : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.builder().create(this)
    }

}

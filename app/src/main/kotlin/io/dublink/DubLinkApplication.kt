package io.dublink

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import io.dublink.inject.DaggerApplicationComponent
import io.dublink.startup.StartupWorkers
import javax.inject.Inject

class DubLinkApplication : DaggerApplication() {

    @Inject lateinit var workers: StartupWorkers

    override fun onCreate() {
        super.onCreate()
        workers.startup(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.factory().create(this)
    }
}

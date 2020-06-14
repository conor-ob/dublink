package io.dublink.startup

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import io.dublink.domain.service.DubLinkProService

class ProcessLifecycleStartupWorker(
    private val dubLinkProService: DubLinkProService
) : StartupWorker, LifecycleObserver {

    override fun startup(application: Application) {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onEnterForeground() {
        dubLinkProService.revokeDubLinkProTrial()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onEnterBackground() {
        dubLinkProService.revokeDubLinkProTrial()
    }
}

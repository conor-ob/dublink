package io.dublink.startup

import android.app.Application

interface StartupWorker {

    fun startup(application: Application)
}

package ie.dublinmapper.startup

import android.app.Application

interface StartupWorker {

    fun startup(application: Application)
}

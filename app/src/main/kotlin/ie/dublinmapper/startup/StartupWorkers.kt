package ie.dublinmapper.startup

import android.app.Application

class StartupWorkers(
    private val workers: List<StartupWorker>
) {

    fun startup(application: Application) {
        workers.forEach { it.startup(application) }
    }
}

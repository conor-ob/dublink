package ie.dublinmapper.init

import android.app.Application

class ApplicationInitializers(
    private val initializers: List<ApplicationInitializer>
) {

    fun initialize(application: Application) {
        initializers.forEach { it.initialize(application) }
    }
}

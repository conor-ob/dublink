package ie.dublinmapper.setup

import android.app.Application

class SetupContainers(
    private val containers: List<SetupContainer>
) {

    fun setup(application: Application) {
        containers.forEach { it.setup(application) }
    }
}

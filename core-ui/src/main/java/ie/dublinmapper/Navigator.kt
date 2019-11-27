package ie.dublinmapper

import io.rtpi.api.ServiceLocation

interface Navigator {

    fun navigateToLiveData(serviceLocation: ServiceLocation)

    fun navigateToSettings()

}

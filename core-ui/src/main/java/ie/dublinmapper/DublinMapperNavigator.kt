package ie.dublinmapper

import io.rtpi.api.ServiceLocation

interface DublinMapperNavigator {

    fun navigateToLiveData(serviceLocation: ServiceLocation)

    fun navigateToSettings()

}

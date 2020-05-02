package ie.dublinmapper

import io.rtpi.api.Service

interface DublinMapperNavigator {

    fun navigateToLiveData(service: Service, locationId: String)

    fun navigateToSearch()

    fun navigateToSettings()

    fun navigateToEditFavourites()
}

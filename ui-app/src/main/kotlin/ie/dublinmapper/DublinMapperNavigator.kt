package ie.dublinmapper

import ie.dublinmapper.domain.model.DubLinkServiceLocation
import io.rtpi.api.Service

interface DublinMapperNavigator {

    fun navigateToLiveData(serviceLocation: DubLinkServiceLocation)

    fun navigateToLiveData(service: Service, locationId: String)

    fun navigateToSearch()

    fun navigateToSettings()

    fun navigateToEditFavourites()
}

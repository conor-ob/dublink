package ie.dublinmapper

import ie.dublinmapper.domain.model.DubLinkServiceLocation

interface DublinMapperNavigator {

    fun navigateToLiveData(serviceLocation: DubLinkServiceLocation)

    fun navigateToSearch()

    fun navigateToSettings()

    fun navigateToEditFavourites()
}

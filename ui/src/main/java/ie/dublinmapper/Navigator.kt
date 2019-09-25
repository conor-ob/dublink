package ie.dublinmapper

import ie.dublinmapper.domain.model.ServiceLocation

interface Navigator {

    fun navigateFavouritesToSearch()

    fun navigateFavouritesToLiveData(serviceLocation: ServiceLocation)

    fun navigateSearchToLiveData(serviceLocation: ServiceLocation)

    fun navigateLiveDataToSettings()

}

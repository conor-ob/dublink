package ie.dublinmapper

import io.rtpi.api.ServiceLocation

interface Navigator {

    fun navigateFavouritesToNearby()

    fun navigateFavouritesToSearch()

    fun navigateFavouritesToLiveData(serviceLocation: ServiceLocation)

    fun navigateNearbyToLiveData(serviceLocation: ServiceLocation)

    fun navigateSearchToLiveData(serviceLocation: ServiceLocation)

    fun navigateLiveDataToSettings()

}

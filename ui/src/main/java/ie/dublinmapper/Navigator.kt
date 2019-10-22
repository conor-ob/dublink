package ie.dublinmapper

import ie.dublinmapper.domain.model.DetailedServiceLocation

interface Navigator {

    fun navigateFavouritesToSearch()

    fun navigateFavouritesToLiveData(serviceLocation: DetailedServiceLocation)

    fun navigateSearchToLiveData(serviceLocation: DetailedServiceLocation)

    fun navigateLiveDataToSettings()

}

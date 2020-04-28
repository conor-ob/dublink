package ie.dublinmapper.domain.datamodel

import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation

interface FavouriteServiceLocationLocalResource {

    fun insertFavourite(serviceLocation: ServiceLocation)

    fun deleteFavourite(serviceLocationId: String, service: Service)

    fun saveChanges(serviceLocations: List<ServiceLocation>)
}

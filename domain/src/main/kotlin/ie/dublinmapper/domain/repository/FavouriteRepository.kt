package ie.dublinmapper.domain.repository

import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation

interface FavouriteRepository {

    fun saveFavourite(serviceLocation: ServiceLocation)

    fun removeFavourite(serviceLocationId: String, service: Service)

    fun saveChanges(serviceLocations: List<ServiceLocation>)
}

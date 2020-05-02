package ie.dublinmapper.domain.repository

import ie.dublinmapper.domain.model.DubLinkServiceLocation
import io.rtpi.api.Service

interface FavouriteRepository {

    fun saveFavourite(serviceLocation: DubLinkServiceLocation)

    fun removeFavourite(serviceLocationId: String, service: Service)

    fun saveChanges(serviceLocations: List<DubLinkServiceLocation>)
}

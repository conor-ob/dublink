package ie.dublinmapper.domain.datamodel

import ie.dublinmapper.domain.model.DubLinkServiceLocation
import io.rtpi.api.Service

interface FavouriteServiceLocationLocalResource {

    fun insertFavourite(serviceLocation: DubLinkServiceLocation)

    fun deleteFavourite(serviceLocationId: String, service: Service)

    fun saveChanges(serviceLocations: List<DubLinkServiceLocation>)
}

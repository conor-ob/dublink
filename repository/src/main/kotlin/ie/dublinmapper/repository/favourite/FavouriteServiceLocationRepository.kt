package ie.dublinmapper.repository.favourite

import ie.dublinmapper.domain.datamodel.FavouriteServiceLocationLocalResource
import ie.dublinmapper.domain.model.DubLinkServiceLocation
import ie.dublinmapper.domain.repository.FavouriteRepository
import io.rtpi.api.Service

class FavouriteServiceLocationRepository(
    private val localResource: FavouriteServiceLocationLocalResource
) : FavouriteRepository {

    override fun saveFavourite(serviceLocation: DubLinkServiceLocation) {
        localResource.insertFavourite(serviceLocation)
    }

    override fun removeFavourite(serviceLocationId: String, service: Service) {
        localResource.deleteFavourite(serviceLocationId, service)
    }

    override fun saveChanges(serviceLocations: List<DubLinkServiceLocation>) {
        localResource.saveChanges(serviceLocations)
    }
}

package ie.dublinmapper.repository.favourite

import ie.dublinmapper.domain.datamodel.FavouriteServiceLocationLocalResource
import ie.dublinmapper.domain.repository.FavouriteRepository
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation

class FavouriteServiceLocationRepository(
    private val localResource: FavouriteServiceLocationLocalResource
) : FavouriteRepository {

    override fun saveFavourite(serviceLocationId: String, serviceLocationName: String, service: Service) {
        localResource.insertFavourite(serviceLocationId, serviceLocationName, service)
    }

    override fun removeFavourite(serviceLocationId: String, service: Service) {
        localResource.deleteFavourite(serviceLocationId, service)
    }

    override fun nameToBeDetermined(serviceLocation: ServiceLocation) {
        localResource.nameToBeDetermined(serviceLocation)
    }
}

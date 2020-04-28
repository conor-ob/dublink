package ie.dublinmapper.repository.favourite

import ie.dublinmapper.domain.datamodel.FavouriteServiceLocationLocalResource
import ie.dublinmapper.domain.repository.FavouriteRepository
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation

class FavouriteServiceLocationRepository(
    private val localResource: FavouriteServiceLocationLocalResource
) : FavouriteRepository {

    override fun saveFavourite(serviceLocation: ServiceLocation) {
        localResource.insertFavourite(serviceLocation)
    }

    override fun removeFavourite(serviceLocationId: String, service: Service) {
        localResource.deleteFavourite(serviceLocationId, service)
    }

    override fun saveChanges(serviceLocations: List<ServiceLocation>) {
        localResource.saveChanges(serviceLocations)
    }
}

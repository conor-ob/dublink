package io.dublink.repository.favourite

import io.dublink.domain.datamodel.FavouriteServiceLocationLocalResource
import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.repository.FavouriteRepository
import io.dublink.domain.service.EnabledServiceManager
import io.reactivex.Observable
import io.rtpi.api.Service

class FavouriteServiceLocationRepository(
    private val localResource: FavouriteServiceLocationLocalResource,
    private val enabledServiceManager: EnabledServiceManager
) : FavouriteRepository {

    override fun getFavourites(): Observable<List<DubLinkServiceLocation>> {
        return localResource.getFavourites()
            .map { favourites ->
                favourites.filter { favourite ->
                    enabledServiceManager.isServiceEnabled(favourite.service)
                }
            }
    }

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
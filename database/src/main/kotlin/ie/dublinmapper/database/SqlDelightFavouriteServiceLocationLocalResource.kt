package ie.dublinmapper.database

import ie.dublinmapper.domain.datamodel.FavouriteServiceLocationLocalResource
import io.rtpi.api.Service

class SqlDelightFavouriteServiceLocationLocalResource(
    private val database: Database
) : FavouriteServiceLocationLocalResource {

    override fun insertFavourite(serviceLocationId: String, serviceLocationName: String, service: Service) {
        database.favouriteLocationsQueries.insertOrReplace(
            locationId = serviceLocationId,
            name = serviceLocationName,
            service = service
        )
    }

    override fun deleteFavourite(serviceLocationId: String, service: Service) {
        database.favouriteLocationsQueries.deleteByIdAndService(
            locationId = serviceLocationId,
            service = service
        )
    }
}

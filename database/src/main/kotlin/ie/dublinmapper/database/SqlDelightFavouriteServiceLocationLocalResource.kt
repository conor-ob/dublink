package ie.dublinmapper.database

import ie.dublinmapper.domain.datamodel.FavouriteServiceLocationLocalResource
import io.rtpi.api.Service

class SqlDelightFavouriteServiceLocationLocalResource(
    private val database: Database
) : FavouriteServiceLocationLocalResource {

    override fun insertFavourite(serviceLocationId: String, serviceLocationName: String, service: Service) {
        database.favouriteLocationQueries.insertOrReplace(
            service = service,
            locationId = serviceLocationId,
            name = serviceLocationName
        )
    }

    override fun deleteFavourite(serviceLocationId: String, service: Service) {
        database.favouriteLocationQueries.delete(
            service = service,
            locationId = serviceLocationId
        )
    }
}

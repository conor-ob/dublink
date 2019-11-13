package ie.dublinmapper.database

import ie.dublinmapper.datamodel.FavouriteServiceLocationLocalResource
import io.rtpi.api.Service

class SqlDelightFavouriteServiceLocationLocalResource(
    private val database: Database
) : FavouriteServiceLocationLocalResource {

    override fun insertFavourite(serviceLocationId: String, serviceLocationName: String, service: Service) {
        database.favouriteServiceLocationEntityQueries.insertOrReplace(
            id = serviceLocationId,
            name = serviceLocationName,
            service = service
        )
    }

    override fun deleteFavourite(serviceLocationId: String, service: Service) {
        database.favouriteServiceLocationEntityQueries.deleteByIdAndService(
            id = serviceLocationId,
            service = service
        )
    }

}

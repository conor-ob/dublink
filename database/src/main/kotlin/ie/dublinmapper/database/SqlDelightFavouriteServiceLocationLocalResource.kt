package ie.dublinmapper.database

import ie.dublinmapper.domain.datamodel.FavouriteServiceLocationLocalResource
import ie.dublinmapper.domain.model.getCustomName
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation

class SqlDelightFavouriteServiceLocationLocalResource(
    private val database: Database
) : FavouriteServiceLocationLocalResource {

    override fun insertFavourite(serviceLocation: ServiceLocation) {
        val count = database.favouriteLocationQueries.count().executeAsOne()
        database.favouriteLocationQueries.insertOrReplace(
            service = serviceLocation.service,
            locationId = serviceLocation.id,
            name = serviceLocation.getCustomName(),
            sortIndex = count
        )
    }

    override fun deleteFavourite(serviceLocationId: String, service: Service) {
        database.favouriteLocationQueries.delete(
            service = service,
            locationId = serviceLocationId
        )
    }

    override fun saveChanges(serviceLocations: List<ServiceLocation>) {
        database.transaction {
            database.favouriteLocationQueries.deleteAll()
            serviceLocations.forEachIndexed { index, serviceLocation ->
                database.favouriteLocationQueries.insertOrReplace(
                    service = serviceLocation.service,
                    locationId = serviceLocation.id,
                    name = serviceLocation.getCustomName(),
                    sortIndex = index.toLong()
                )
            }
        }
    }
}

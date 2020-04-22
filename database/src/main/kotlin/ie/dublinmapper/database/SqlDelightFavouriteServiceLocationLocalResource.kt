package ie.dublinmapper.database

import ie.dublinmapper.domain.datamodel.FavouriteServiceLocationLocalResource
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation

class SqlDelightFavouriteServiceLocationLocalResource(
    private val database: Database
) : FavouriteServiceLocationLocalResource {

    override fun insertFavourite(serviceLocationId: String, serviceLocationName: String, service: Service) {
        val count = database.favouriteLocationQueries.count().executeAsOne()
        database.favouriteLocationQueries.insertOrReplace(
            service = service,
            locationId = serviceLocationId,
            name = serviceLocationName,
            sortIndex = count
        )
    }

    override fun deleteFavourite(serviceLocationId: String, service: Service) {
        database.favouriteLocationQueries.delete(
            service = service,
            locationId = serviceLocationId
        )
    }

    override fun nameToBeDetermined(serviceLocation: ServiceLocation) {
        val entities = database.favouriteLocationQueries
            .selectAll()
            .executeAsList()

        val matchingIndex = entities
            .indexOfFirst { it.service == serviceLocation.service && it.locationId == serviceLocation.id }

        val copy = entities.toMutableList()

        val entity = copy.removeAt(matchingIndex)

        val newSortOrder = listOf(entity).plus(copy)
        database.transaction {
            database.favouriteLocationQueries.deleteAll()
            newSortOrder.forEachIndexed { index, favouriteLocationEntity ->
                database.favouriteLocationQueries.insertOrReplace(
                    service = favouriteLocationEntity.service,
                    locationId = favouriteLocationEntity.locationId,
                    name = favouriteLocationEntity.name,
                    sortIndex = index.toLong()
                )
            }
        }
    }
}

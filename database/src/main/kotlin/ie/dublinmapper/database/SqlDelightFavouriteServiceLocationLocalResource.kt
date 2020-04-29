package ie.dublinmapper.database

import ie.dublinmapper.domain.datamodel.FavouriteServiceLocationLocalResource
import ie.dublinmapper.domain.model.getCustomDirections
import ie.dublinmapper.domain.model.getCustomName
import ie.dublinmapper.domain.model.getCustomRoutes
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation

class SqlDelightFavouriteServiceLocationLocalResource(
    private val database: Database
) : FavouriteServiceLocationLocalResource {

    override fun insertFavourite(serviceLocation: ServiceLocation) {
        database.transaction {
            val count = database.favouriteLocationQueries.count().executeAsOne()
            database.favouriteLocationQueries.insertOrReplace(
                service = serviceLocation.service,
                locationId = serviceLocation.id,
                name = serviceLocation.getCustomName(),
                sortIndex = count
            )
            for (routeGroup in serviceLocation.getCustomRoutes()) {
                for (route in routeGroup.routes) {
                    database.favouriteServiceQueries.insertOrReplace(
                        service = serviceLocation.service,
                        locationId = serviceLocation.id,
                        route = route,
                        operator = routeGroup.operator
                    )
                }
            }
            for (direction in serviceLocation.getCustomDirections()) {
                database.favouriteDirectionQueries.insertOrReplace(
                    service = serviceLocation.service,
                    locationId = serviceLocation.id,
                    direction = direction
                )
            }
        }
    }

    override fun deleteFavourite(serviceLocationId: String, service: Service) {
        database.transaction {
            database.favouriteLocationQueries.delete(
                service = service,
                locationId = serviceLocationId
            )
            database.favouriteServiceQueries.delete(
                service = service,
                locationId = serviceLocationId
            )
            database.favouriteDirectionQueries.delete(
                service = service,
                locationId = serviceLocationId
            )
        }
    }

    override fun saveChanges(serviceLocations: List<ServiceLocation>) {
        database.transaction {
            database.favouriteLocationQueries.deleteAll()
            database.favouriteServiceQueries.deleteAll()
            database.favouriteDirectionQueries.deleteAll()
            serviceLocations.forEachIndexed { index, serviceLocation ->
                database.favouriteLocationQueries.insertOrReplace(
                    service = serviceLocation.service,
                    locationId = serviceLocation.id,
                    name = serviceLocation.getCustomName(),
                    sortIndex = index.toLong()
                )
            }
            for (serviceLocation in serviceLocations) {
                for (routeGroup in serviceLocation.getCustomRoutes()) {
                    for (route in routeGroup.routes) {
                        database.favouriteServiceQueries.insertOrReplace(
                            service = serviceLocation.service,
                            locationId = serviceLocation.id,
                            route = route,
                            operator = routeGroup.operator
                        )
                    }
                }
                for (direction in serviceLocation.getCustomDirections()) {
                    database.favouriteDirectionQueries.insertOrReplace(
                        service = serviceLocation.service,
                        locationId = serviceLocation.id,
                        direction = direction
                    )
                }
            }
        }
    }
}

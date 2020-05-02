package ie.dublinmapper.database

import ie.dublinmapper.domain.datamodel.FavouriteServiceLocationLocalResource
import ie.dublinmapper.domain.model.DubLinkServiceLocation
import ie.dublinmapper.domain.model.DubLinkStopLocation
import ie.dublinmapper.domain.model.Filter
import io.rtpi.api.Service

class SqlDelightFavouriteServiceLocationLocalResource(
    private val database: Database
) : FavouriteServiceLocationLocalResource {

    override fun insertFavourite(serviceLocation: DubLinkServiceLocation) {
        database.transaction {
            val count = database.favouriteLocationQueries.count().executeAsOne()
            database.favouriteLocationQueries.insertOrReplace(
                service = serviceLocation.service,
                locationId = serviceLocation.id,
                name = serviceLocation.name,
                sortIndex = count
            )
            if (serviceLocation is DubLinkStopLocation) {
                for (filter in serviceLocation.filters.filter { it.isActive }) {
                    when (filter) {
                        is Filter.RouteFilter -> {
                            database.favouriteServiceQueries.insertOrReplace(
                                service = serviceLocation.service,
                                locationId = serviceLocation.id,
                                route = filter.route.id,
                                operator = filter.route.operator
                            )
                        }
                        is Filter.DirectionFilter -> {
                            database.favouriteDirectionQueries.insertOrReplace(
                                service = serviceLocation.service,
                                locationId = serviceLocation.id,
                                direction = filter.direction
                            )
                        }
                    }
                }
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

    override fun saveChanges(serviceLocations: List<DubLinkServiceLocation>) {
        database.transaction {
            database.favouriteLocationQueries.deleteAll()
            database.favouriteServiceQueries.deleteAll()
            database.favouriteDirectionQueries.deleteAll()
            serviceLocations.forEachIndexed { index, serviceLocation ->
                database.favouriteLocationQueries.insertOrReplace(
                    service = serviceLocation.service,
                    locationId = serviceLocation.id,
                    name = serviceLocation.name,
                    sortIndex = index.toLong()
                )
            }
            for (serviceLocation in serviceLocations) {
                if (serviceLocation is DubLinkStopLocation) {
                    for (filter in serviceLocation.filters.filter { it.isActive }) {
                        when (filter) {
                            is Filter.RouteFilter -> {
                                database.favouriteServiceQueries.insertOrReplace(
                                    service = serviceLocation.service,
                                    locationId = serviceLocation.id,
                                    route = filter.route.id,
                                    operator = filter.route.operator
                                )
                            }
                            is Filter.DirectionFilter -> {
                                database.favouriteDirectionQueries.insertOrReplace(
                                    service = serviceLocation.service,
                                    locationId = serviceLocation.id,
                                    direction = filter.direction
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

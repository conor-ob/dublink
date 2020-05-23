package io.dublink.database

import com.squareup.sqldelight.runtime.rx.asObservable
import com.squareup.sqldelight.runtime.rx.mapToList
import io.dublink.domain.datamodel.FavouriteServiceLocationLocalResource
import io.dublink.domain.model.DubLinkDockLocation
import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.model.DubLinkStopLocation
import io.dublink.domain.model.FavouriteMetadata
import io.dublink.domain.model.Filter
import io.dublink.domain.model.Route
import io.dublink.domain.model.withFavouriteMetadata
import io.reactivex.Observable
import io.reactivex.functions.Function3
import io.rtpi.api.Coordinate
import io.rtpi.api.DockLocation
import io.rtpi.api.RouteGroup
import io.rtpi.api.Service
import io.rtpi.api.StopLocation

class SqlDelightFavouriteServiceLocationLocalResource(
    private val database: Database
) : FavouriteServiceLocationLocalResource {

    override fun getFavourites(): Observable<List<DubLinkServiceLocation>> {
        val locationsObservable = database.favouriteLocationQueries
            .selectAll()
            .asObservable()
            .mapToList()

        val servicesObservable = database.favouriteServiceQueries
            .selectAll()
            .asObservable()
            .mapToList()

        val directionsObservable = database.favouriteDirectionQueries
            .selectAll()
            .asObservable()
            .mapToList()

        return Observable.zip(
            locationsObservable,
            servicesObservable,
            directionsObservable,
            Function3 { locationEntities, serviceEntities, directionEntities ->
                aggregate(locationEntities, serviceEntities, directionEntities)
            }
        )
    }

    private fun aggregate(
        locationEntities: List<FavouriteLocationEntity>,
        serviceEntities: List<FavouriteServiceEntity>,
        directionEntities: List<FavouriteDirectionEntity>
    ): List<DubLinkServiceLocation> {
        val serviceEntitiesByLocation = serviceEntities.groupBy { it.locationId }
        val directionEntitiesByLocation = directionEntities.groupBy { it.locationId }
        return locationEntities.map { entity ->
            when (entity.service) {
                Service.AIRCOACH,
                Service.BUS_EIREANN,
                Service.DUBLIN_BUS,
                Service.IRISH_RAIL,
                Service.LUAS -> {
                    val directionEntitiesForLocation = directionEntitiesByLocation[entity.locationId]
                    val serviceEntitiesForLocation = serviceEntitiesByLocation[entity.locationId]
                    val routeGroups = serviceEntitiesForLocation?.groupBy { serviceEntity ->
                        serviceEntity.operator
                    }?.map { serviceEntities ->
                        RouteGroup(
                            operator = serviceEntities.key,
                            routes = serviceEntities.value.map { serviceEntity ->
                                serviceEntity.route
                            }
                        )
                    } ?: emptyList()
                    DubLinkStopLocation(
                        StopLocation(
                            id = entity.locationId,
                            name = entity.name,
                            service = entity.service,
                            coordinate = Coordinate(entity.latitude, entity.longitude),
                            routeGroups = routeGroups
                        )
                    ).withFavouriteMetadata(
                        favouriteMetadata = FavouriteMetadata(
                            isFavourite = true,
                            name = entity.name,
                            sortIndex = entity.sortIndex.toInt(),
                            routes = routeGroups.flatMap { routeGroup ->
                                routeGroup.routes.map { route ->
                                    Route(operator = routeGroup.operator, id = route)
                                }
                            },
                            directions = directionEntitiesForLocation?.map {
                                it.direction
                            } ?: emptyList()
                        )
                    )
                }
                Service.DUBLIN_BIKES -> DubLinkDockLocation(
                    DockLocation(
                        id = entity.locationId,
                        name = entity.name,
                        service = entity.service,
                        coordinate = Coordinate(entity.latitude, entity.longitude),
                        availableBikes = 0,
                        availableDocks = 0,
                        totalDocks = 0
                    )
                ).withFavouriteMetadata(
                    favouriteMetadata = FavouriteMetadata(
                        isFavourite = true,
                        name = entity.name,
                        sortIndex = entity.sortIndex.toInt()
                    )
                )
            }
        }
    }

    override fun saveFavourites(serviceLocations: List<DubLinkServiceLocation>) {
        database.transaction {
            serviceLocations.forEach { serviceLocation ->
                database.favouriteLocationQueries.delete(service = serviceLocation.service, locationId = serviceLocation.id)
                database.favouriteServiceQueries.delete(service = serviceLocation.service, locationId = serviceLocation.id)
                database.favouriteDirectionQueries.delete(service = serviceLocation.service, locationId = serviceLocation.id)
                val newSortIndex: Long = if (serviceLocation.favouriteSortIndex == -1) {
                    database.favouriteLocationQueries.count().executeAsOne()
                } else {
                    serviceLocation.favouriteSortIndex.toLong()
                }
                database.favouriteLocationQueries.insertOrReplace(
                    service = serviceLocation.service,
                    locationId = serviceLocation.id,
                    name = serviceLocation.name,
                    latitude = serviceLocation.coordinate.latitude,
                    longitude = serviceLocation.coordinate.longitude,
                    sortIndex = newSortIndex
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
}

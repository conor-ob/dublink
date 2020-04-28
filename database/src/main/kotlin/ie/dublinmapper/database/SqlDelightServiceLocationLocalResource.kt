package ie.dublinmapper.database

import com.squareup.sqldelight.runtime.rx.asObservable
import com.squareup.sqldelight.runtime.rx.mapToList
import ie.dublinmapper.domain.datamodel.ServiceLocationLocalResource
import ie.dublinmapper.domain.model.addCustomRoute
import ie.dublinmapper.domain.model.setCustomName
import ie.dublinmapper.domain.model.setFavourite
import ie.dublinmapper.domain.model.setSortIndex
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function4
import io.rtpi.api.Coordinate
import io.rtpi.api.DockLocation
import io.rtpi.api.Operator
import io.rtpi.api.RouteGroup
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation
import io.rtpi.api.StopLocation

class SqlDelightServiceLocationLocalResource(
    private val database: Database
) : ServiceLocationLocalResource {

    override fun selectServiceLocations(service: Service): Observable<List<ServiceLocation>> {
        val locationsObservable = when (service) {
            Service.AIRCOACH -> database.aircoachLocationQueries
                .selectAll { id, name, latitude, longitude ->
                    toLocationEntity(id, name, latitude, longitude)
                }
            Service.BUS_EIREANN -> database.busEireannLocationQueries
                .selectAll { id, name, latitude, longitude ->
                    toLocationEntity(id, name, latitude, longitude)
                }
            Service.DUBLIN_BIKES -> database.dublinBikesLocationQueries
                .selectAll { id, name, latitude, longitude ->
                    toLocationEntity(id, name, latitude, longitude)
                }
            Service.DUBLIN_BUS -> database.dublinBusLocationQueries
                .selectAll { id, name, latitude, longitude ->
                    toLocationEntity(id, name, latitude, longitude)
                }
            Service.IRISH_RAIL -> database.irishRailLocationQueries
                .selectAll { id, name, latitude, longitude ->
                    toLocationEntity(id, name, latitude, longitude)
                }
            Service.LUAS -> database.luasLocationQueries
                .selectAll { id, name, latitude, longitude ->
                    toLocationEntity(id, name, latitude, longitude)
                }
        }
            .asObservable()
            .mapToList()

        val servicesObservable = when (service) {
            Service.AIRCOACH -> database.aircoachServiceQueries
                .selectAll { _, locationId, route, operator ->
                    toServiceEntity(locationId, route, operator)
                }
            Service.BUS_EIREANN -> database.busEireannServiceQueries
                .selectAll { _, locationId, route, operator ->
                    toServiceEntity(locationId, route, operator)
                }
            Service.DUBLIN_BIKES -> database.dublinBikesServiceQueries
                .selectAll { _, locationId, availableBikes, availableDocks, totalDocks ->
                    toServiceEntity(locationId, availableBikes, availableDocks, totalDocks)
                }
            Service.DUBLIN_BUS -> database.dublinBusServiceQueries
                .selectAll { _, locationId, route, operator ->
                    toServiceEntity(locationId, route, operator)
                }
            Service.IRISH_RAIL -> database.irishRailServiceQueries
                .selectAll { _, locationId, route, operator ->
                    toServiceEntity(locationId, route, operator)
                }
            Service.LUAS -> database.luasServiceQueries
                .selectAll { _, locationId, route, operator ->
                    toServiceEntity(locationId, route, operator)
                }
        }
            .asObservable()
            .mapToList()

        val favouritesObservable = database.favouriteLocationQueries
            .selectAllByService(service) { _, _, locationId, name, sortIndex ->
                toFavouriteEntity(locationId, name, sortIndex)
            }
            .asObservable()
            .mapToList()

        val favouriteServicesObservable = database.favouriteServiceQueries
            .selectAllByService(service) { _, service, locationId, route, operator ->
                toFavouriteServiceEntity(service, locationId, route, operator)
            }
            .asObservable()
            .mapToList()

        return Observable.zip(
            locationsObservable,
            servicesObservable,
            favouritesObservable,
            favouriteServicesObservable,
            Function4 { locationEntities, serviceEntities, favouriteEntities, favouriteServices ->
                resolve(service, locationEntities, serviceEntities, favouriteEntities, favouriteServices)
            }
        )
    }

    private fun resolve(
        service: Service,
        locationEntities: List<LocationEntity>,
        serviceEntities: List<ServiceEntity>,
        favouriteEntities: List<FavouriteEntity>,
        favouriteServiceEntities: List<FavouriteServiceEntityTemp>
    ): List<ServiceLocation> {
        val serviceEntitiesByLocation = serviceEntities.groupBy { it.locationId }

        val favouriteServiceEntitiesByLocation = favouriteServiceEntities.groupBy { it.locationId }

        val serviceLocations = when (service) {
            Service.AIRCOACH,
            Service.BUS_EIREANN,
            Service.DUBLIN_BUS,
            Service.IRISH_RAIL,
            Service.LUAS -> locationEntities.map {
                val serviceEntitiesForLocation = serviceEntitiesByLocation[it.id]
                val routeGroups = if (serviceEntitiesForLocation != null &&
                    serviceEntitiesForLocation.all { serviceEntity ->
                        serviceEntity is StopServiceEntity
                    }
                ) {
                    serviceEntitiesForLocation
                        .groupBy { serviceEntity ->
                            (serviceEntity as StopServiceEntity).operator
                        }
                        .map { serviceEntities ->
                            RouteGroup(
                                operator = serviceEntities.key,
                                routes = serviceEntities.value.map { serviceEntity ->
                                    (serviceEntity as StopServiceEntity).route
                                }
                            )
                        }
                } else {
                    emptyList()
                }
                StopLocation(
                    id = it.id,
                    name = it.name,
                    service = service,
                    coordinate = it.coordinate,
                    routeGroups = routeGroups
                )
            }
            Service.DUBLIN_BIKES -> locationEntities.map {
                val serviceEntitiesForLocation = serviceEntitiesByLocation[it.id]
                val serviceEntity = if (serviceEntitiesForLocation != null &&
                    serviceEntitiesForLocation.isNotEmpty() &&
                    serviceEntitiesForLocation.all { serviceEntity ->
                        serviceEntity is DockServiceEntity
                    }
                ) {
                    serviceEntitiesForLocation.first()
                } else {
                    null
                }
                DockLocation(
                    id = it.id,
                    name = it.name,
                    service = service,
                    coordinate = it.coordinate,
                    availableBikes = (serviceEntity as? DockServiceEntity)?.availableBikes ?: 0,
                    availableDocks = (serviceEntity as? DockServiceEntity)?.availableDocks ?: 0,
                    totalDocks = (serviceEntity as? DockServiceEntity)?.totalDocks ?: 0
                )
            }
        }
            .associateBy { it.id }
            .toMutableMap()

        favouriteEntities.forEach {
            val favourite = serviceLocations[it.locationId]
            if (favourite != null) {
                var updated: ServiceLocation = favourite
                updated = updated.setFavourite()
                updated = updated.setSortIndex(it.sortIndex)
                updated = updated.setCustomName(it.name)

                val favouriteServices = favouriteServiceEntitiesByLocation[it.locationId]
                if (favouriteServices != null) {
                    for (favouriteService in favouriteServices) {
                        updated = updated.addCustomRoute(favouriteService.operator, favouriteService.route)
                    }
                }

                serviceLocations[it.locationId] = updated
            }
        }

        return serviceLocations.values.toList()
    }

    private fun toFavouriteEntity(
        locationId: String,
        name: String?,
        sortIndex: Long
    ) = FavouriteEntity(locationId, name, sortIndex)

    private fun toFavouriteServiceEntity(
        service: Service,
        locationId: String,
        route: String,
        operator: Operator
    ) = FavouriteServiceEntityTemp(service, locationId, route, operator)

    private fun toServiceEntity(
        locationId: String,
        availableBikes: Int,
        availableDocks: Int,
        totalDocks: Int
    ): ServiceEntity = DockServiceEntity(
        locationId, availableDocks, availableBikes, totalDocks
    )

    private fun toServiceEntity(
        locationId: String,
        route: String,
        operator: Operator
    ): ServiceEntity = StopServiceEntity(
        locationId, route, operator
    )

    private fun toLocationEntity(
        id: String,
        name: String,
        latitude: Double,
        longitude: Double
    ) = LocationEntity(
        id = id,
        name = name,
        coordinate = Coordinate(latitude, longitude)
    )

    override fun insertServiceLocations(service: Service, serviceLocations: List<ServiceLocation>) {
        database.transaction {
            deleteLocations(service)
            for (serviceLocation in serviceLocations) {
                insertLocations(service, serviceLocation)
                insertServices(service, serviceLocation)
            }
        }
    }

    private fun insertServices(service: Service, serviceLocation: ServiceLocation) {
        when (service) {
            Service.AIRCOACH -> {
                if (serviceLocation is StopLocation) {
                    for (routeGroup in serviceLocation.routeGroups) {
                        for (route in routeGroup.routes) {
                            database.aircoachServiceQueries.insertOrReplace(
                                locationId = serviceLocation.id,
                                route = route,
                                operator = routeGroup.operator
                            )
                        }
                    }
                }
            }
            Service.BUS_EIREANN -> {
                if (serviceLocation is StopLocation) {
                    for (routeGroup in serviceLocation.routeGroups) {
                        for (route in routeGroup.routes) {
                            database.busEireannServiceQueries.insertOrReplace(
                                locationId = serviceLocation.id,
                                route = route,
                                operator = routeGroup.operator
                            )
                        }
                    }
                }
            }
            Service.DUBLIN_BIKES -> {
                if (serviceLocation is DockLocation) {
                    database.dublinBikesServiceQueries.insertOrReplace(
                        locationId = serviceLocation.id,
                        availableBikes = serviceLocation.availableBikes,
                        availableDocks = serviceLocation.availableDocks,
                        totalDocks = serviceLocation.totalDocks
                    )
                }
            }
            Service.DUBLIN_BUS -> {
                if (serviceLocation is StopLocation) {
                    for (routeGroup in serviceLocation.routeGroups) {
                        for (route in routeGroup.routes) {
                            database.dublinBusServiceQueries.insertOrReplace(
                                locationId = serviceLocation.id,
                                route = route,
                                operator = routeGroup.operator
                            )
                        }
                    }
                }
            }
            Service.IRISH_RAIL -> {
                if (serviceLocation is StopLocation) {
                    for (routeGroup in serviceLocation.routeGroups) {
                        for (route in routeGroup.routes) {
                            database.irishRailServiceQueries.insertOrReplace(
                                locationId = serviceLocation.id,
                                route = route,
                                operator = routeGroup.operator
                            )
                        }
                    }
                }
            }
            Service.LUAS -> {
                if (serviceLocation is StopLocation) {
                    for (routeGroup in serviceLocation.routeGroups) {
                        for (route in routeGroup.routes) {
                            database.luasServiceQueries.insertOrReplace(
                                locationId = serviceLocation.id,
                                route = route,
                                operator = routeGroup.operator
                            )
                        }
                    }
                }
            }
        }
    }

    private fun insertLocations(service: Service, serviceLocation: ServiceLocation) {
        when (service) {
            Service.AIRCOACH -> database.aircoachLocationQueries.insertOrReplace(
                id = serviceLocation.id,
                name = serviceLocation.name,
                latitude = serviceLocation.coordinate.latitude,
                longitude = serviceLocation.coordinate.longitude
            )
            Service.BUS_EIREANN -> database.busEireannLocationQueries.insertOrReplace(
                id = serviceLocation.id,
                name = serviceLocation.name,
                latitude = serviceLocation.coordinate.latitude,
                longitude = serviceLocation.coordinate.longitude
            )
            Service.DUBLIN_BIKES -> database.dublinBikesLocationQueries.insertOrReplace(
                id = serviceLocation.id,
                name = serviceLocation.name,
                latitude = serviceLocation.coordinate.latitude,
                longitude = serviceLocation.coordinate.longitude
            )
            Service.DUBLIN_BUS -> database.dublinBusLocationQueries.insertOrReplace(
                id = serviceLocation.id,
                name = serviceLocation.name,
                latitude = serviceLocation.coordinate.latitude,
                longitude = serviceLocation.coordinate.longitude
            )
            Service.IRISH_RAIL -> database.irishRailLocationQueries.insertOrReplace(
                id = serviceLocation.id,
                name = serviceLocation.name,
                latitude = serviceLocation.coordinate.latitude,
                longitude = serviceLocation.coordinate.longitude
            )
            Service.LUAS -> database.luasLocationQueries.insertOrReplace(
                id = serviceLocation.id,
                name = serviceLocation.name,
                latitude = serviceLocation.coordinate.latitude,
                longitude = serviceLocation.coordinate.longitude
            )
        }
    }

    private fun deleteLocations(service: Service) {
        when (service) {
            Service.AIRCOACH -> {
                database.aircoachLocationQueries.deleteAll()
                database.aircoachServiceQueries.deleteAll()
            }
            Service.BUS_EIREANN -> {
                database.busEireannLocationQueries.deleteAll()
                database.busEireannServiceQueries.deleteAll()
            }
            Service.DUBLIN_BIKES -> {
                database.dublinBikesLocationQueries.deleteAll()
                database.dublinBikesServiceQueries.deleteAll()
            }
            Service.DUBLIN_BUS -> {
                database.dublinBusLocationQueries.deleteAll()
                database.dublinBusServiceQueries.deleteAll()
            }
            Service.IRISH_RAIL -> {
                database.irishRailLocationQueries.deleteAll()
                database.irishRailServiceQueries.deleteAll()
            }
            Service.LUAS -> {
                database.luasLocationQueries.deleteAll()
                database.luasServiceQueries.deleteAll()
            }
        }
    }
}

data class LocationEntity(
    val id: String,
    val name: String,
    val coordinate: Coordinate
)

data class FavouriteEntity(
    val locationId: String,
    val name: String?,
    val sortIndex: Long
)

data class FavouriteServiceEntityTemp(
    val service: Service,
    val locationId: String,
    val route: String,
    val operator: Operator
)

interface ServiceEntity {
    val locationId: String
}

data class StopServiceEntity(
    override val locationId: String,
    val route: String,
    val operator: Operator
) : ServiceEntity

data class DockServiceEntity(
    override val locationId: String,
    val availableDocks: Int,
    val availableBikes: Int,
    val totalDocks: Int
) : ServiceEntity

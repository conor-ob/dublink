package ie.dublinmapper.database

import com.squareup.sqldelight.runtime.rx.asObservable
import com.squareup.sqldelight.runtime.rx.mapToList
import ie.dublinmapper.domain.datamodel.ServiceLocationLocalResource
import ie.dublinmapper.domain.model.setFavourite
import io.reactivex.Observable
import io.reactivex.functions.Function3
import io.rtpi.api.*

class SqlDelightServiceLocationLocalResource(
    private val database: Database
) : ServiceLocationLocalResource {

    override fun selectServiceLocations(service: Service): Observable<List<ServiceLocation>> {
        val locationsObservable = when (service) {
            Service.AIRCOACH -> database.aircoachLocationsQueries
                .selectAll { id, name, latitude, longitude ->
                    toLocationEntity(id, name, latitude, longitude)
                }
            Service.BUS_EIREANN -> database.busEireannLocationsQueries
                .selectAll { id, name, latitude, longitude ->
                    toLocationEntity(id, name, latitude, longitude)
                }
            Service.DUBLIN_BIKES -> database.dublinBikesLocationsQueries
                .selectAll { id, name, latitude, longitude ->
                    toLocationEntity(id, name, latitude, longitude)
                }
            Service.DUBLIN_BUS -> database.dublinBusLocationsQueries
                .selectAll { id, name, latitude, longitude ->
                    toLocationEntity(id, name, latitude, longitude)
                }
            Service.IRISH_RAIL -> database.irishRailLocationsQueries
                .selectAll { id, name, latitude, longitude ->
                    toLocationEntity(id, name, latitude, longitude)
                }
            Service.LUAS -> database.luasLocationsQueries
                .selectAll { id, name, latitude, longitude ->
                    toLocationEntity(id, name, latitude, longitude)
                }
        }
            .asObservable()
            .mapToList()

        val servicesObservable = when (service) {
            Service.AIRCOACH -> database.aircoachServicesQueries
                .selectAll { _, locationId, route, operator ->
                    toServiceEntity(locationId, route, operator)
                }
            Service.BUS_EIREANN -> database.busEireannServicesQueries
                .selectAll { _, locationId, route, operator ->
                    toServiceEntity(locationId, route, operator)
                }
            Service.DUBLIN_BIKES -> database.dublinBikesServicesQueries
                .selectAll { _, locationId, availableBikes, availableDocks, totalDocks ->
                    toServiceEntity(locationId, availableBikes, availableDocks, totalDocks)
                }
            Service.DUBLIN_BUS -> database.dublinBusServicesQueries
                .selectAll { _, locationId, route, operator ->
                    toServiceEntity(locationId, route, operator)
                }
            Service.IRISH_RAIL -> database.irishRailServicesQueries
                .selectAll { _, locationId, route, operator ->
                    toServiceEntity(locationId, route, operator)
                }
            Service.LUAS -> database.luasServicesQueries
                .selectAll { _, locationId, route, operator ->
                    toServiceEntity(locationId, route, operator)
                }
        }
            .asObservable()
            .mapToList()


        val favouritesObservable = database.favouriteLocationsQueries
            .selectAllByService(service) { _, _, locationId, _ ->
                toFavouriteEntity(locationId)
            }
            .asObservable()
            .mapToList()


        return Observable.zip(
            locationsObservable,
            servicesObservable,
            favouritesObservable,
            Function3 { locationEntities, serviceEntities, favouriteEntities ->
                resolve(service, locationEntities, serviceEntities, favouriteEntities)
            }
        )
    }

    private fun resolve(
        service: Service,
        locationEntities: List<LocationEntity>,
        serviceEntities: List<ServiceEntity>,
        favouriteEntities: List<FavouriteEntity>
    ): List<ServiceLocation> {
        val serviceEntitiesByLocation = serviceEntities.groupBy { it.locationId }

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
                    routes = routeGroups
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
        }.associateBy { it.id }

        favouriteEntities.forEach {
            serviceLocations[it.locationId]?.setFavourite()
        }

        return serviceLocations.values.toList()
    }

    private fun toFavouriteEntity(
        locationId: String
    ) = FavouriteEntity(
        locationId
    )

    private fun toServiceEntity(
        locationId: String,
        availableBikes: Int,
        availableDocks: Int,
        totalDocks: Int
    ): ServiceEntity  = DockServiceEntity(
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
                    for (routeGroup in serviceLocation.routes) {
                        for (route in routeGroup.routes) {
                            database.aircoachServicesQueries.insertOrReplace(
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
                    for (routeGroup in serviceLocation.routes) {
                        for (route in routeGroup.routes) {
                            database.busEireannServicesQueries.insertOrReplace(
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
                    database.dublinBikesServicesQueries.insertOrReplace(
                        locationId = serviceLocation.id,
                        availableBikes = serviceLocation.availableBikes,
                        availableDocks = serviceLocation.availableDocks,
                        totalDocks = serviceLocation.totalDocks
                    )
                }
            }
            Service.DUBLIN_BUS -> {
                if (serviceLocation is StopLocation) {
                    for (routeGroup in serviceLocation.routes) {
                        for (route in routeGroup.routes) {
                            database.dublinBusServicesQueries.insertOrReplace(
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
                    for (routeGroup in serviceLocation.routes) {
                        for (route in routeGroup.routes) {
                            database.irishRailServicesQueries.insertOrReplace(
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
                    for (routeGroup in serviceLocation.routes) {
                        for (route in routeGroup.routes) {
                            database.luasServicesQueries.insertOrReplace(
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
            Service.AIRCOACH -> database.aircoachLocationsQueries.insertOrReplace(
                id = serviceLocation.id,
                name = serviceLocation.name,
                latitude = serviceLocation.coordinate.latitude,
                longitude = serviceLocation.coordinate.longitude
            )
            Service.BUS_EIREANN -> database.busEireannLocationsQueries.insertOrReplace(
                id = serviceLocation.id,
                name = serviceLocation.name,
                latitude = serviceLocation.coordinate.latitude,
                longitude = serviceLocation.coordinate.longitude
            )
            Service.DUBLIN_BIKES -> database.dublinBikesLocationsQueries.insertOrReplace(
                id = serviceLocation.id,
                name = serviceLocation.name,
                latitude = serviceLocation.coordinate.latitude,
                longitude = serviceLocation.coordinate.longitude
            )
            Service.DUBLIN_BUS -> database.dublinBusLocationsQueries.insertOrReplace(
                id = serviceLocation.id,
                name = serviceLocation.name,
                latitude = serviceLocation.coordinate.latitude,
                longitude = serviceLocation.coordinate.longitude
            )
            Service.IRISH_RAIL -> database.irishRailLocationsQueries.insertOrReplace(
                id = serviceLocation.id,
                name = serviceLocation.name,
                latitude = serviceLocation.coordinate.latitude,
                longitude = serviceLocation.coordinate.longitude
            )
            Service.LUAS -> database.luasLocationsQueries.insertOrReplace(
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
                database.aircoachLocationsQueries.deleteAll()
                database.aircoachServicesQueries.deleteAll()
            }
            Service.BUS_EIREANN -> {
                database.busEireannLocationsQueries.deleteAll()
                database.busEireannServicesQueries.deleteAll()
            }
            Service.DUBLIN_BIKES -> {
                database.dublinBikesLocationsQueries.deleteAll()
                database.dublinBikesServicesQueries.deleteAll()
            }
            Service.DUBLIN_BUS -> {
                database.dublinBusLocationsQueries.deleteAll()
                database.dublinBusServicesQueries.deleteAll()
            }
            Service.IRISH_RAIL -> {
                database.irishRailLocationsQueries.deleteAll()
                database.irishRailServicesQueries.deleteAll()
            }
            Service.LUAS -> {
                database.luasLocationsQueries.deleteAll()
                database.luasServicesQueries.deleteAll()
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
    val locationId: String
//    val name: String,
//    val service: Service
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

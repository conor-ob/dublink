package ie.dublinmapper.database

import com.squareup.sqldelight.runtime.rx.asObservable
import com.squareup.sqldelight.runtime.rx.mapToList
import ie.dublinmapper.datamodel.DublinBusStopLocalResource
import ie.dublinmapper.domain.model.setFavourite
import io.reactivex.Observable
import io.reactivex.functions.Function3
import io.rtpi.api.Coordinate
import io.rtpi.api.DublinBusStop
import io.rtpi.api.Route
import io.rtpi.api.Service
import io.rtpi.util.RouteComparator

class SqlDelightDublinBusStopLocalResource(
    private val database: Database
) : DublinBusStopLocalResource {

    override fun selectStops(): Observable<List<DublinBusStop>> {
        return Observable.zip(
            database.dublinBusStopLocationEntityQueries.selectAll()
                .asObservable()
                .mapToList(),
            database.dublinBusStopServiceEntityQueries.selectAll()
                .asObservable()
                .mapToList(),
            database.favouriteServiceLocationEntityQueries.selectAllByService(Service.DUBLIN_BUS)
                .asObservable()
                .mapToList(),
            Function3 {
                    locationEntities,
                    serviceEntities,
                    favouriteEntities ->
                resolve(
                    locationEntities,
                    serviceEntities,
                    favouriteEntities
                )
            }
        )
    }

    private fun resolve(
        locationEntities: List<DublinBusStopLocationEntity>,
        serviceEntities: List<DublinBusStopServiceEntity>,
        favouriteEntities: List<FavouriteServiceLocationEntity>
    ): List<DublinBusStop> {
        val serviceEntitiesByLocation = serviceEntities
            .groupBy { it.locationId }

        val locations = locationEntities.map {
            val routes = serviceEntitiesByLocation[it.id]?.map { thing -> Route(thing.route, thing.operator) }
            return@map DublinBusStop(
                id = it.id,
                name = it.name,
                coordinate = Coordinate(it.latitude, it.longitude),
                routes = routes?.sortedWith(RouteComparator) ?: emptyList(),
                operators = routes?.map { route -> route.operator }?.toSet() ?: emptySet()
            )
        }.associateBy { it.id }

        favouriteEntities.forEach {
            locations[it.id]?.setFavourite()
        }

        return locations.values.toList()
    }

//    private fun selectStopsNew(): Observable<List<DublinBusStop>> {
//        return database.dublinBusStopLocationEntityQueries.selectAllTest()
//            .asObservable()
//            .mapToList()
//            .map { entities ->
//                entities
//                    .groupBy { entity -> entity.locationId }
//                    .mapValues {
//                        val routes = it.value.mapNotNull { hello ->
//                            if (hello.operator != null && hello.route != null) {
//                                return@mapNotNull Route(hello.route!!, hello.operator!!)
//                            }
//                            return@mapNotNull null
//                        }
//
//                        val defaultEntity = it.value.first()
//                        val dublinBusStop = DublinBusStop(
//                            id = defaultEntity.locationId,
//                            name = defaultEntity.name,
//                            coordinate = Coordinate(defaultEntity.latitude, defaultEntity.longitude),
//                            routes = routes,
//                            operators = routes.map { rte -> rte.operator }.toSet()
//                        )
//                        if (defaultEntity.favouriteId != null) {
//                            dublinBusStop.setFavourite()
//                        }
//                        return@mapValues dublinBusStop
//                    }
//                    .values.toList()
//            }
//    }
//
//    private fun selectFavouritesNew(): Observable<List<DublinBusStop>> {
//        return database.dublinBusStopLocationEntityQueries.selectAllFavouritesTest()
//            .asObservable()
//            .mapToList()
//            .map { entities ->
//                entities
//                    .groupBy { entity -> entity.locationId }
//                    .mapValues {
//                        val routes = it.value.mapNotNull { hello ->
//                            if (hello.operator != null && hello.route != null) {
//                                return@mapNotNull Route(hello.route!!, hello.operator!!)
//                            }
//                            return@mapNotNull null
//                        }
//
//                        val defaultEntity = it.value.first()
//                        val dublinBusStop = DublinBusStop(
//                            id = defaultEntity.locationId,
//                            name = defaultEntity.name,
//                            coordinate = Coordinate(defaultEntity.latitude, defaultEntity.longitude),
//                            routes = routes,
//                            operators = routes.map { rte -> rte.operator }.toSet()
//                        )
//                        dublinBusStop.setFavourite()
//                        return@mapValues dublinBusStop
//                    }
//                    .values.toList()
//            }
//    }

    override fun insertStops(stops: List<DublinBusStop>) {
        database.transaction {
            database.dublinBusStopLocationEntityQueries.deleteAll()
            database.dublinBusStopServiceEntityQueries.deleteAll()
            for (stop in stops) {
                database.dublinBusStopLocationEntityQueries.insertOrReplace(
                    id = stop.id,
                    name = stop.name,
                    latitude = stop.coordinate.latitude,
                    longitude = stop.coordinate.longitude
                )
                for (route in stop.routes) {
                    database.dublinBusStopServiceEntityQueries.insertOrReplace(
                        operator = route.operator,
                        route = route.id,
                        locationId = stop.id
                    )
                }
            }
        }
    }

}

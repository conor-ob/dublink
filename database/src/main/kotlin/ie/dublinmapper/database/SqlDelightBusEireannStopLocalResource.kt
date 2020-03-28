package ie.dublinmapper.database

import com.squareup.sqldelight.runtime.rx.asObservable
import com.squareup.sqldelight.runtime.rx.mapToList
import ie.dublinmapper.domain.datamodel.BusEireannStopLocalResource
import ie.dublinmapper.domain.model.setFavourite
import io.reactivex.Observable
import io.reactivex.functions.Function3
import io.rtpi.api.BusEireannStop
import io.rtpi.api.Coordinate
import io.rtpi.api.Route
import io.rtpi.api.Service
import io.rtpi.util.RouteComparator

class SqlDelightBusEireannStopLocalResource(
    private val database: Database
) : BusEireannStopLocalResource {

    override fun selectStops(): Observable<List<BusEireannStop>> {
        return Observable.zip(
            database.busEireannStopLocationEntityQueries.selectAll()
                .asObservable()
                .mapToList(),
            database.busEireannStopServiceEntityQueries.selectAll()
                .asObservable()
                .mapToList(),
            database.favouriteServiceLocationEntityQueries.selectAllByService(Service.BUS_EIREANN)
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
        locationEntities: List<BusEireannStopLocationEntity>,
        serviceEntities: List<BusEireannStopServiceEntity>,
        favouriteEntities: List<FavouriteServiceLocationEntity>
    ): List<BusEireannStop> {
        val serviceEntitiesByLocation = serviceEntities
            .groupBy { it.locationId }

        val locations = locationEntities.map {
            val routes = serviceEntitiesByLocation[it.id]?.map { thing -> Route(thing.route, thing.operator) }
            return@map BusEireannStop(
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

    override fun insertStops(stops: List<BusEireannStop>) {
        database.transaction {
            database.busEireannStopLocationEntityQueries.deleteAll()
            database.busEireannStopServiceEntityQueries.deleteAll()
            for (stop in stops) {
                database.busEireannStopLocationEntityQueries.insertOrReplace(
                    id = stop.id,
                    name = stop.name,
                    latitude = stop.coordinate.latitude,
                    longitude = stop.coordinate.longitude
                )
                for (route in stop.routes) {
                    database.busEireannStopServiceEntityQueries.insertOrReplace(
                        operator = route.operator,
                        route = route.id,
                        locationId = stop.id
                    )
                }
            }
        }
    }

}

package ie.dublinmapper.database

import com.squareup.sqldelight.runtime.rx.asObservable
import com.squareup.sqldelight.runtime.rx.mapToList
import ie.dublinmapper.domain.datamodel.AircoachStopLocalResource
import ie.dublinmapper.domain.model.setFavourite
import io.reactivex.Observable
import io.reactivex.functions.Function3
import io.rtpi.api.AircoachStop
import io.rtpi.api.Coordinate
import io.rtpi.api.Route
import io.rtpi.api.Service
import io.rtpi.util.RouteComparator

class SqlDelightAircoachStopLocalResource(
    private val database: Database
) : AircoachStopLocalResource {

    override fun selectStops(): Observable<List<AircoachStop>> {
        return Observable.zip(
            database.aircoachStopLocationEntityQueries.selectAll()
                .asObservable()
                .mapToList(),
            database.aircoachStopServiceEntityQueries.selectAll()
                .asObservable()
                .mapToList(),
            database.favouriteServiceLocationEntityQueries.selectAllByService(Service.AIRCOACH)
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
        locationEntities: List<AircoachStopLocationEntity>,
        serviceEntities: List<AircoachStopServiceEntity>,
        favouriteEntities: List<FavouriteServiceLocationEntity>
    ): List<AircoachStop> {
        val serviceEntitiesByLocation = serviceEntities
            .groupBy { it.locationId }

        val locations = locationEntities.map {
            val routes = serviceEntitiesByLocation[it.id]?.map { thing -> Route(thing.route, thing.operator) }
            return@map AircoachStop(
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

    override fun insertStops(stops: List<AircoachStop>) {
        database.transaction {
            database.aircoachStopLocationEntityQueries.deleteAll()
            database.aircoachStopServiceEntityQueries.deleteAll()
            for (stop in stops) {
                database.aircoachStopLocationEntityQueries.insertOrReplace(
                    id = stop.id,
                    name = stop.name,
                    latitude = stop.coordinate.latitude,
                    longitude = stop.coordinate.longitude
                )
                for (route in stop.routes) {
                    database.aircoachStopServiceEntityQueries.insertOrReplace(
                        operator = route.operator,
                        route = route.id,
                        locationId = stop.id
                    )
                }
            }
        }
    }

}

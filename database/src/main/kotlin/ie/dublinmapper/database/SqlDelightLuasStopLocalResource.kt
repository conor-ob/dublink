package ie.dublinmapper.database

import com.squareup.sqldelight.runtime.rx.asObservable
import com.squareup.sqldelight.runtime.rx.mapToList
import ie.dublinmapper.datamodel.LuasStopLocalResource
import ie.dublinmapper.domain.model.setFavourite
import ie.dublinmapper.util.AlphanumComparator
import io.reactivex.Observable
import io.reactivex.functions.Function3
import io.rtpi.api.Coordinate
import io.rtpi.api.LuasStop
import io.rtpi.api.Route
import io.rtpi.api.Service

class SqlDelightLuasStopLocalResource(
    private val database: Database
) : LuasStopLocalResource {

    override fun selectStops(): Observable<List<LuasStop>> {
        return Observable.zip(
            database.luasStopLocationEntityQueries.selectAll()
                .asObservable()
                .mapToList(),
            database.luasStopServiceEntityQueries.selectAll()
                .asObservable()
                .mapToList(),
            database.favouriteServiceLocationEntityQueries.selectAllByService(Service.LUAS)
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
        locationEntities: List<LuasStopLocationEntity>,
        serviceEntities: List<LuasStopServiceEntity>,
        favouriteEntities: List<FavouriteServiceLocationEntity>
    ): List<LuasStop> {
        val serviceEntitiesByLocation = serviceEntities
            .groupBy { it.locationId }

        val locations = locationEntities.map {
            val routes = serviceEntitiesByLocation[it.id]?.map { thing -> Route(thing.route, thing.operator) }
            return@map LuasStop(
                id = it.id,
                name = it.name,
                coordinate = Coordinate(it.latitude, it.longitude),
                routes = routes?.sortedWith(Comparator { r1, r2 ->
                    AlphanumComparator.getInstance().compare(r1.id, r2.id)
                }) ?: emptyList(),
                operators = routes?.map { route -> route.operator }?.toSet() ?: emptySet()
            )
        }.associateBy { it.id }

        favouriteEntities.forEach {
            locations[it.id]?.setFavourite()
        }

        return locations.values.toList()
    }

    override fun insertStops(stops: List<LuasStop>) {
        database.transaction {
            database.luasStopLocationEntityQueries.deleteAll()
            database.luasStopServiceEntityQueries.deleteAll()
            for (stop in stops) {
                database.luasStopLocationEntityQueries.insertOrReplace(
                    id = stop.id,
                    name = stop.name,
                    latitude = stop.coordinate.latitude,
                    longitude = stop.coordinate.longitude
                )
                for (route in stop.routes) {
                    database.luasStopServiceEntityQueries.insertOrReplace(
                        operator = route.operator,
                        route = route.id,
                        locationId = stop.id
                    )
                }
            }
        }
    }

}

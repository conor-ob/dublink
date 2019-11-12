package ie.dublinmapper.database

import com.squareup.sqldelight.runtime.rx.asObservable
import com.squareup.sqldelight.runtime.rx.mapToList
import ie.dublinmapper.datamodel.BusEireannStopLocalResource
import ie.dublinmapper.domain.model.setFavourite
import io.reactivex.Observable
import io.reactivex.functions.Function3
import io.rtpi.api.BusEireannStop
import io.rtpi.api.Coordinate
import io.rtpi.api.Service

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
            val routesByOperator = serviceEntitiesByLocation[it.id]
                ?.groupBy { entity -> entity.operator }
                ?.mapValues { values -> values.value.map { value -> value.route }.sorted() }

            return@map BusEireannStop(
                id = it.id,
                name = it.name,
                coordinate = Coordinate(it.latitude, it.longitude),
                routes = routesByOperator ?: emptyMap(),
                operators = routesByOperator?.keys ?: emptySet()
            )
        }.associateBy { it.id }

        favouriteEntities.forEach {
            locations[it.id]?.setFavourite()
        }

        return locations.values.toList()
    }

    override fun insertStops(stops: List<BusEireannStop>) {
        database.busEireannStopLocationEntityQueries.deleteAll()
        database.busEireannStopServiceEntityQueries.deleteAll()
        for (stop in stops) {
            database.busEireannStopLocationEntityQueries.insertOrReplace(
                id = stop.id,
                name = stop.name,
                latitude = stop.coordinate.latitude,
                longitude = stop.coordinate.longitude
            )
            for (entry in stop.routes) {
                val operator = entry.key
                for (route in entry.value) {
                    database.busEireannStopServiceEntityQueries.insertOrReplace(
                        operator = operator,
                        route = route,
                        locationId = stop.id
                    )
                }
            }
        }
    }

}

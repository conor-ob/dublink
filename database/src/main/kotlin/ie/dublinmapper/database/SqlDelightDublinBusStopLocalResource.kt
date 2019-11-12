package ie.dublinmapper.database

import com.squareup.sqldelight.runtime.rx.asObservable
import com.squareup.sqldelight.runtime.rx.mapToList
import ie.dublinmapper.datamodel.DublinBusStopLocalResource
import ie.dublinmapper.domain.model.setFavourite
import io.reactivex.Observable
import io.reactivex.functions.Function3
import io.rtpi.api.Coordinate
import io.rtpi.api.DublinBusStop
import io.rtpi.api.Service

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
            val routesByOperator = serviceEntitiesByLocation[it.id]
                ?.groupBy { entity -> entity.operator }
                ?.mapValues { values -> values.value.map { value -> value.route }.sorted() }

            return@map DublinBusStop(
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

    override fun insertStops(stops: List<DublinBusStop>) {
        database.dublinBusStopLocationEntityQueries.deleteAll()
        database.dublinBusStopServiceEntityQueries.deleteAll()
        for (stop in stops) {
            database.dublinBusStopLocationEntityQueries.insertOrReplace(
                id = stop.id,
                name = stop.name,
                latitude = stop.coordinate.latitude,
                longitude = stop.coordinate.longitude
            )
            for (entry in stop.routes) {
                val operator = entry.key
                for (route in entry.value) {
                    database.dublinBusStopServiceEntityQueries.insertOrReplace(
                        operator = operator,
                        route = route,
                        locationId = stop.id
                    )
                }
            }
        }
    }

}

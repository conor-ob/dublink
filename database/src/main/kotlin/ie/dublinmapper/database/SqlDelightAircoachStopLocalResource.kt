package ie.dublinmapper.database

import com.squareup.sqldelight.runtime.rx.asObservable
import com.squareup.sqldelight.runtime.rx.mapToList
import ie.dublinmapper.datamodel.AircoachStopLocalResource
import ie.dublinmapper.domain.model.setFavourite
import io.reactivex.Observable
import io.reactivex.functions.Function3
import io.rtpi.api.AircoachStop
import io.rtpi.api.Coordinate
import io.rtpi.api.Service

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
            val routesByOperator = serviceEntitiesByLocation[it.id]
                ?.groupBy { entity -> entity.operator }
                ?.mapValues { values -> values.value.map { value -> value.route }.sorted() }

            return@map AircoachStop(
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

    override fun insertStops(stops: List<AircoachStop>) {
        database.aircoachStopLocationEntityQueries.deleteAll()
        database.aircoachStopServiceEntityQueries.deleteAll()
        for (stop in stops) {
            database.aircoachStopLocationEntityQueries.insertOrReplace(
                id = stop.id,
                name = stop.name,
                latitude = stop.coordinate.latitude,
                longitude = stop.coordinate.longitude
            )
            for (entry in stop.routes) {
                val operator = entry.key
                for (route in entry.value) {
                    database.aircoachStopServiceEntityQueries.insertOrReplace(
                        operator = operator,
                        route = route,
                        locationId = stop.id
                    )
                }
            }
        }
    }

}

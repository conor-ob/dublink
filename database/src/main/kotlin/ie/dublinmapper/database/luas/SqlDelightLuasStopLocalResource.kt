package ie.dublinmapper.database.luas

import com.squareup.sqldelight.runtime.rx.asObservable
import com.squareup.sqldelight.runtime.rx.mapToList
import ie.dublinmapper.database.Database
import ie.dublinmapper.database.FavouriteServiceLocationEntity
import ie.dublinmapper.database.LuasStopLocationEntity
import ie.dublinmapper.database.LuasStopServiceEntity
import ie.dublinmapper.datamodel.luas.*
import ie.dublinmapper.domain.model.setFavourite
import io.reactivex.Observable
import io.reactivex.functions.Function3
import io.rtpi.api.Coordinate
import io.rtpi.api.LuasStop
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
            Observable.just(emptyList<FavouriteServiceLocationEntity>()),
//            database.favouriteServiceLocationEntityQueries.selectAllByService(Service.LUAS)
//                .asObservable()
//                .mapToList(),
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
            val routesByOperator = serviceEntitiesByLocation[it.id]
                ?.groupBy { entity -> entity.operator }
                ?.mapValues { values -> values.value.map { value -> value.route }.sorted() }

            return@map LuasStop(
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

    override fun insertStops(stops: List<LuasStop>) {
        database.luasStopLocationEntityQueries.deleteAll()
        database.luasStopServiceEntityQueries.deleteAll()
        for (stop in stops) {
            database.luasStopLocationEntityQueries.insertOrReplace(
                id = stop.id,
                name = stop.name,
                latitude = stop.coordinate.latitude,
                longitude = stop.coordinate.longitude
            )
            for (entry in stop.routes) {
                val operator = entry.key
                for (route in entry.value) {
                    database.luasStopServiceEntityQueries.insertOrReplace(
                        operator = operator,
                        route = route,
                        locationId = stop.id
                    )
                }
            }
        }
    }

}

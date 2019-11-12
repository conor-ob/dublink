package ie.dublinmapper.database

import com.squareup.sqldelight.runtime.rx.asObservable
import com.squareup.sqldelight.runtime.rx.mapToList
import ie.dublinmapper.datamodel.AircoachStopLocalResource
import io.reactivex.Observable
import io.reactivex.functions.Function3
import io.rtpi.api.AircoachStop
import io.rtpi.api.Coordinate

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
//            database.favouriteServiceLocationEntityQueries.selectAllByService(Service.AIRCOACH)
//                .asObservable()
//                .mapToList(),
            Observable.just(emptyList<FavouriteServiceLocationEntity>()),
            Function3 {
                    aircoachStopEntities,
                    aircoachStopServiceEntities,
                    favoriteAircoachStopEntities ->
                resolve(
                    aircoachStopEntities,
                    aircoachStopServiceEntities,
                    favoriteAircoachStopEntities
                )
            }
        )
    }

    private fun resolve(
        aircoachStopEntities: List<AircoachStopLocationEntity>,
        aircoachStopServiceEntities: List<AircoachStopServiceEntity>,
        favoriteAircoachStopEntities: List<FavouriteServiceLocationEntity>
    ): List<AircoachStop> {
        val aircoachStopStopServiceEntitiesByLocation = aircoachStopServiceEntities
            .groupBy { it.locationId }

        val aircoachStops = aircoachStopEntities.map {
            val serviceEntities = aircoachStopStopServiceEntitiesByLocation[it.id]
                ?.groupBy { entity -> entity.operator }
                ?.mapValues { values -> values.value.map { value -> value.route }.sorted() }

            return@map AircoachStop(
                id = it.id,
                name = it.name,
                coordinate = Coordinate(it.latitude, it.longitude),
                routes = serviceEntities ?: emptyMap(),
                operators = serviceEntities?.keys ?: emptySet()
            )
        }.associateBy { it.id }

//        favoriteAircoachStopEntities.forEach {
//            irishRailStations[it.id]?.setFavourite()
//        }

        return aircoachStops.values.toList()
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

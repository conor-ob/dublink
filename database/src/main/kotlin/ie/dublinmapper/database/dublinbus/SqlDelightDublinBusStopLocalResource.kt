package ie.dublinmapper.database.dublinbus

import com.squareup.sqldelight.runtime.rx.asObservable
import com.squareup.sqldelight.runtime.rx.mapToList
import ie.dublinmapper.database.Database
import ie.dublinmapper.database.DublinBusStopLocationEntity
import ie.dublinmapper.database.DublinBusStopServiceEntity
import ie.dublinmapper.database.FavouriteServiceLocationEntity
import ie.dublinmapper.datamodel.dublinbus.DublinBusStopLocalResource
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
                    dublinBusStopEntities,
                    dublinBusStopServiceEntities,
                    favoriteIrishRailStationEntities ->
                resolve(
                    dublinBusStopEntities,
                    dublinBusStopServiceEntities,
                    favoriteIrishRailStationEntities
                )
            }
        )
    }

    private fun resolve(
        dublinBusStopEntities: List<DublinBusStopLocationEntity>,
        dublinBusStopServiceEntities: List<DublinBusStopServiceEntity>,
        favoriteIrishRailStationEntities: List<FavouriteServiceLocationEntity>
    ): List<DublinBusStop> {
        val dublinBusStopServiceEntitiesByLocation = dublinBusStopServiceEntities
            .groupBy { it.locationId }

        val dublinBusStops = dublinBusStopEntities.map {
            val serviceEntities = dublinBusStopServiceEntitiesByLocation[it.id]
                ?.groupBy { entity -> entity.operator }
                ?.mapValues { values -> values.value.map { value -> value.route }.sorted() }

            return@map DublinBusStop(
                id = it.id,
                name = it.name,
                coordinate = Coordinate(it.latitude, it.longitude),
                routes = serviceEntities ?: emptyMap(),
                operators = serviceEntities?.keys ?: emptySet()
            )
        }.associateBy { it.id }

//        favoriteIrishRailStationEntities.forEach {
//            irishRailStations[it.id]?.setFavourite()
//        }

        return dublinBusStops.values.toList()
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

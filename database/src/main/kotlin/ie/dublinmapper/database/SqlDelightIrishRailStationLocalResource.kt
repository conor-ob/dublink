package ie.dublinmapper.database

import com.squareup.sqldelight.runtime.rx.asObservable
import com.squareup.sqldelight.runtime.rx.mapToList
import ie.dublinmapper.datamodel.IrishRailStationLocalResource
import ie.dublinmapper.domain.model.setFavourite
import io.reactivex.Observable
import io.reactivex.functions.Function3
import io.rtpi.api.Coordinate
import io.rtpi.api.IrishRailStation
import io.rtpi.api.Operator
import io.rtpi.api.Service

class SqlDelightIrishRailStationLocalResource(
    private val database: Database
) : IrishRailStationLocalResource {

    override fun selectStations(): Observable<List<IrishRailStation>> {
        return Observable.zip(
            database.irishRailStationLocationEntityQueries.selectAll()
                .asObservable()
                .mapToList(),
            database.irishRailStationServiceEntityQueries.selectAll()
                .asObservable()
                .mapToList(),
            database.favouriteServiceLocationEntityQueries.selectAllByService(Service.IRISH_RAIL)
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
        locationEntities: List<IrishRailStationLocationEntity>,
        serviceEntities: List<IrishRailStationServiceEntity>,
        favouriteEntities: List<FavouriteServiceLocationEntity>
    ): List<IrishRailStation> {
        val serviceEntitiesByLocation = serviceEntities
            .groupBy { it.locationId }

        val locations = locationEntities.map {
            IrishRailStation(
                id = it.id,
                name = it.name,
                coordinate = Coordinate(it.latitude, it.longitude),
                operators = serviceEntitiesByLocation
                    .getValue(it.id)
                    .map { entity -> entity.operator }
                    .toSortedSet(
                        Comparator { o1, o2 ->
                            if (o1 == Operator.DART) {
                                return@Comparator -1
                            }
                            return@Comparator o1.name.compareTo(o2.name)
                        }
                    )
            )
        }.associateBy { it.id }

        favouriteEntities.forEach {
            locations[it.id]?.setFavourite()
        }

        return locations.values.toList()
    }

    override fun insertStations(stations: List<IrishRailStation>) {
        database.transaction {
            database.irishRailStationLocationEntityQueries.deleteAll()
            database.irishRailStationServiceEntityQueries.deleteAll()
            for (station in stations) {
                database.irishRailStationLocationEntityQueries.insertOrReplace(
                    id = station.id,
                    name = station.name,
                    latitude = station.coordinate.latitude,
                    longitude = station.coordinate.longitude
                )
                for (operator in station.operators) {
                    database.irishRailStationServiceEntityQueries.insertOrReplace(
                        operator = operator,
                        locationId = station.id
                    )
                }
            }
        }
    }

}

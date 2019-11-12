package ie.dublinmapper.database.irishrail

import com.squareup.sqldelight.runtime.rx.asObservable
import com.squareup.sqldelight.runtime.rx.mapToList
import ie.dublinmapper.database.Database
import ie.dublinmapper.database.FavouriteServiceLocationEntity
import ie.dublinmapper.database.IrishRailStationLocationEntity
import ie.dublinmapper.database.IrishRailStationServiceEntity
import ie.dublinmapper.datamodel.irishrail.*
import ie.dublinmapper.domain.model.setFavourite
import io.reactivex.Observable
import io.reactivex.functions.Function3
import io.rtpi.api.Coordinate
import io.rtpi.api.IrishRailStation
import io.rtpi.api.Operator
import io.rtpi.api.Service
import kotlin.Comparator

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
            Observable.just(emptyList<FavouriteServiceLocationEntity>()),
//            database.favouriteServiceLocationEntityQueries.selectAllByService(Service.IRISH_RAIL)
//                .asObservable()
//                .mapToList(),
            Function3 {
                    irishRailStationEntities,
                    irishRailStationServiceEntities,
                    favoriteIrishRailStationEntities ->
                resolve(
                    irishRailStationEntities,
                    irishRailStationServiceEntities,
                    favoriteIrishRailStationEntities
                )
            }
        )
    }

    private fun resolve(
        irishRailStationEntities: List<IrishRailStationLocationEntity>,
        irishRailStationServiceEntities: List<IrishRailStationServiceEntity>,
        favoriteIrishRailStationEntities: List<FavouriteServiceLocationEntity>
    ): List<IrishRailStation> {
        val irishRailStationServiceEntitiesByLocation = irishRailStationServiceEntities
            .groupBy { it.locationId }

        val irishRailStations = irishRailStationEntities.map {
            IrishRailStation(
                id = it.id,
                name = it.name,
                coordinate = Coordinate(it.latitude, it.longitude),
                operators = irishRailStationServiceEntitiesByLocation
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

        favoriteIrishRailStationEntities.forEach {
            irishRailStations[it.id]?.setFavourite()
        }

        return irishRailStations.values.toList()
    }

    override fun insertStations(stations: List<IrishRailStation>) {
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

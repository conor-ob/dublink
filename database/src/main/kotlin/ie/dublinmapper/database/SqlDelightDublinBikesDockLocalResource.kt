package ie.dublinmapper.database

import com.squareup.sqldelight.runtime.rx.asObservable
import com.squareup.sqldelight.runtime.rx.mapToList
import ie.dublinmapper.datamodel.DublinBikesDockLocalResource
import ie.dublinmapper.domain.model.setFavourite
import io.reactivex.Observable
import io.reactivex.functions.Function3
import io.rtpi.api.Coordinate
import io.rtpi.api.DublinBikesDock
import io.rtpi.api.Service

class SqlDelightDublinBikesDockLocalResource(
    private val database: Database
) : DublinBikesDockLocalResource {

    override fun selectDocks(): Observable<List<DublinBikesDock>> {
        return Observable.zip(
            database.dublinBikesDockLocationEntityQueries.selectAll()
                .asObservable()
                .mapToList(),
            database.dublinBikesDockServiceEntityQueries.selectAll()
                .asObservable()
                .mapToList(),
            database.favouriteServiceLocationEntityQueries.selectAllByService(Service.DUBLIN_BIKES)
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
        locationEntities: List<DublinBikesDockLocationEntity>,
        serviceEntities: List<DublinBikesDockServiceEntity>,
        favouriteEntities: List<FavouriteServiceLocationEntity>
    ): List<DublinBikesDock> {
        val serviceEntitiesByLocation = serviceEntities
            .groupBy { it.locationId }

        val locations = locationEntities.map {
            val service = serviceEntitiesByLocation.getValue(it.id).first()

            return@map DublinBikesDock(
                id = it.id,
                name = it.name,
                coordinate = Coordinate(it.latitude, it.longitude),
                operators = setOf(service.operator),
                docks = service.docks,
                availableBikes = service.availableBikes,
                availableDocks = service.availableDocks
            )
        }.associateBy { it.id }

        favouriteEntities.forEach {
            locations[it.id]?.setFavourite()
        }

        return locations.values.toList()
    }

    override fun insertDocks(docks: List<DublinBikesDock>) {
        database.dublinBikesDockLocationEntityQueries.deleteAll()
        database.dublinBikesDockServiceEntityQueries.deleteAll()
        for (dock in docks) {
            database.dublinBikesDockLocationEntityQueries.insertOrReplace(
                id = dock.id,
                name = dock.name,
                latitude = dock.coordinate.latitude,
                longitude = dock.coordinate.longitude
            )
            for (operator in dock.operators) {
                database.dublinBikesDockServiceEntityQueries.insertOrReplace(
                    operator = operator,
                    docks = dock.docks,
                    availableDocks = dock.availableDocks,
                    availableBikes = dock.availableBikes,
                    locationId = dock.id
                )
            }
        }
    }

}

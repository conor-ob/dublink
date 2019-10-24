package ie.dublinmapper.repository.irishrail.stations

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import ie.dublinmapper.datamodel.irishrail.*
import ie.dublinmapper.datamodel.persister.PersisterDao
import ie.dublinmapper.domain.model.Favourite
import ie.dublinmapper.domain.model.setFavourite
import ie.dublinmapper.repository.AbstractPersister
import ie.dublinmapper.util.InternetManager
import io.reactivex.Maybe
import io.reactivex.functions.BiFunction
import io.rtpi.api.IrishRailStation
import io.rtpi.api.Service
import ma.glasnost.orika.MapperFacade

class IrishRailStationPersister(
    private val localResource: IrishRailStationLocalResource,
    private val mapper: MapperFacade,
    memoryPolicy: MemoryPolicy,
    persisterDao: PersisterDao,
    internetManager: InternetManager
) : AbstractPersister<List<IrishRailStation>, List<IrishRailStation>, Service>(memoryPolicy, persisterDao, internetManager) {

    override fun select(key: Service): Maybe<List<IrishRailStation>> {
        return Maybe.zip(
            localResource.selectStations().map { mapper.mapAsList(it, IrishRailStation::class.java) },
            localResource.selectFavouriteStations().map { mapper.mapAsList(it, Favourite::class.java) },
            BiFunction { irishRailStations, favourites -> resolve(irishRailStations, favourites) }
        )
    }

    private fun resolve(irishRailStations: List<IrishRailStation>, favourites: List<Favourite>): List<IrishRailStation> {
        val irishRailStationsById = irishRailStations.associateBy { it.id }.toMutableMap()
        for (favourite in favourites) {
            val irishRailStation = irishRailStationsById[favourite.id]
            if (irishRailStation != null) {
                // may be deactivated after a user has saved it as a favourite
                irishRailStation.setFavourite()
            }
        }
        return irishRailStationsById.values.toList()
    }

    override fun insert(key: Service, raw: List<IrishRailStation>) {
        val entities = mapper.mapAsList(raw, IrishRailStationEntity::class.java)
        localResource.insertStations(entities)
    }

}

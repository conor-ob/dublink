package ie.dublinmapper.repository.dublinbikes.docks

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import ie.dublinmapper.datamodel.dublinbikes.*
import ie.dublinmapper.datamodel.persister.PersisterDao
import ie.dublinmapper.domain.model.Favourite
import ie.dublinmapper.domain.model.setFavourite
import ie.dublinmapper.repository.AbstractPersister
import ie.dublinmapper.util.InternetManager
import io.reactivex.Maybe
import io.reactivex.functions.BiFunction
import io.rtpi.api.DublinBikesDock
import io.rtpi.api.Service
import ma.glasnost.orika.MapperFacade

class DublinBikesDockPersister(
    private val localResource: DublinBikesDockLocalResource,
    private val mapper: MapperFacade,
    memoryPolicy: MemoryPolicy,
    persisterDao: PersisterDao,
    internetManager: InternetManager
) : AbstractPersister<List<DublinBikesDock>, List<DublinBikesDock>, Service>(memoryPolicy, persisterDao, internetManager) {

    override fun select(key: Service): Maybe<List<DublinBikesDock>> {
        return Maybe.zip(
            localResource.selectDocks().map { mapper.mapAsList(it, DublinBikesDock::class.java) },
            localResource.selectFavouriteDocks().map { mapper.mapAsList(it, Favourite::class.java) },
            BiFunction { dublinBikesDock, favourites -> resolve(dublinBikesDock, favourites) }
        )
    }

    private fun resolve(luasStops: List<DublinBikesDock>, favourites: List<Favourite>): List<DublinBikesDock> {
        val dublinBikesDocksById = luasStops.associateBy { it.id }.toMutableMap()
        for (favourite in favourites) {
            val dublinBikesDock = dublinBikesDocksById[favourite.id]
            if (dublinBikesDock != null) {
                // may be deactivated after a user has saved it as a favourite
                dublinBikesDock.setFavourite()
            }
        }
        return dublinBikesDocksById.values.toList()
    }

    override fun insert(key: Service, raw: List<DublinBikesDock>) {
        val entities = mapper.mapAsList(raw, DublinBikesDockEntity::class.java)
        localResource.insertDocks(entities)
    }

}

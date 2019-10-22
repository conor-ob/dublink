package ie.dublinmapper.repository.luas.stops

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import ie.dublinmapper.datamodel.luas.*
import ie.dublinmapper.datamodel.persister.PersisterDao
import ie.dublinmapper.domain.model.DetailedLuasStop
import ie.dublinmapper.domain.model.Favourite
import ie.dublinmapper.repository.AbstractPersister
import ie.dublinmapper.util.InternetManager
import io.reactivex.Maybe
import io.reactivex.functions.BiFunction
import io.rtpi.api.LuasStop
import io.rtpi.api.Service
import ma.glasnost.orika.MapperFacade

class LuasStopPersister(
    private val localResource: LuasStopLocalResource,
    private val mapper: MapperFacade,
    memoryPolicy: MemoryPolicy,
    persisterDao: PersisterDao,
    internetManager: InternetManager
) : AbstractPersister<List<LuasStop>, List<DetailedLuasStop>, Service>(memoryPolicy, persisterDao, internetManager) {

    override fun select(key: Service): Maybe<List<DetailedLuasStop>> {
        return Maybe.zip(
            localResource.selectStops().map { mapper.mapAsList(it, DetailedLuasStop::class.java) },
            localResource.selectFavouriteStops().map { mapper.mapAsList(it, Favourite::class.java) },
            BiFunction { luasStops, favourites -> resolve(luasStops, favourites) }
        )
    }

    private fun resolve(luasStops: List<DetailedLuasStop>, favourites: List<Favourite>): List<DetailedLuasStop> {
        val luasStopsById = luasStops.associateBy { it.id }.toMutableMap()
        for (favourite in favourites) {
            val luasStop = luasStopsById[favourite.id]
            if (luasStop != null) {
                // may be deactivated after a user has saved it as a favourite
                val luasStopWithFavourite = luasStop.copy(favourite = favourite)
                luasStopsById[luasStop.id] = luasStopWithFavourite
            }
        }
        return luasStopsById.values.toList()
    }

    override fun insert(key: Service, raw: List<LuasStop>) {
        val entities = mapper.mapAsList(raw, LuasStopEntity::class.java)
        localResource.insertStops(entities)
    }

}

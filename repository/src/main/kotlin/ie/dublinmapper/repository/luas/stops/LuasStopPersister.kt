package ie.dublinmapper.repository.luas.stops

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import ie.dublinmapper.datamodel.TxRunner
import ie.dublinmapper.datamodel.favourite.FavouriteDao
import ie.dublinmapper.datamodel.luas.*
import ie.dublinmapper.datamodel.persister.PersisterDao
import ie.dublinmapper.domain.model.Favourite
import ie.dublinmapper.domain.model.LuasStop
import ie.dublinmapper.repository.AbstractPersister
import ie.dublinmapper.service.rtpi.RtpiBusStopInformationJson
import ie.dublinmapper.util.InternetManager
import ie.dublinmapper.util.Service
import io.reactivex.Maybe
import io.reactivex.functions.BiFunction
import ma.glasnost.orika.MapperFacade

class LuasStopPersister(
    private val localResource: LuasStopLocalResource,
    private val mapper: MapperFacade,
    memoryPolicy: MemoryPolicy,
    persisterDao: PersisterDao,
    internetManager: InternetManager
) : AbstractPersister<List<RtpiBusStopInformationJson>, List<LuasStop>, Service>(memoryPolicy, persisterDao, internetManager) {

    override fun select(key: Service): Maybe<List<LuasStop>> {
        return Maybe.zip(
            localResource.selectStops().map { mapper.mapAsList(it, LuasStop::class.java) },
            localResource.selectFavouriteStops().map { mapper.mapAsList(it, Favourite::class.java) },
            BiFunction { luasStops, favourites -> resolve(luasStops, favourites) }
        )
    }

    private fun resolve(luasStops: List<LuasStop>, favourites: List<Favourite>): List<LuasStop> {
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

    override fun insert(key: Service, raw: List<RtpiBusStopInformationJson>) {
        val entities = mapper.mapAsList(raw, LuasStopEntity::class.java)
        localResource.insertStops(entities)
    }

}

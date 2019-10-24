package ie.dublinmapper.repository.buseireann.stops

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import ie.dublinmapper.datamodel.buseireann.*
import ie.dublinmapper.datamodel.persister.PersisterDao
import ie.dublinmapper.domain.model.Favourite
import ie.dublinmapper.domain.model.setFavourite
import ie.dublinmapper.repository.AbstractPersister
import ie.dublinmapper.util.InternetManager
import io.reactivex.Maybe
import io.reactivex.functions.BiFunction
import io.rtpi.api.BusEireannStop
import io.rtpi.api.Service
import ma.glasnost.orika.MapperFacade

class BusEireannStopPersister(
    private val localResource: BusEireannStopLocalResource,
    private val mapper: MapperFacade,
    memoryPolicy: MemoryPolicy,
    persisterDao: PersisterDao,
    internetManager: InternetManager
) : AbstractPersister<List<BusEireannStop>, List<BusEireannStop>, Service>(memoryPolicy, persisterDao, internetManager) {

    override fun select(key: Service): Maybe<List<BusEireannStop>> {
        return Maybe.zip(
            localResource.selectStops().map { mapper.mapAsList(it, BusEireannStop::class.java) },
            localResource.selectFavouriteStops().map { mapper.mapAsList(it, Favourite::class.java) },
            BiFunction { busEireannStops, favourites -> resolve(busEireannStops, favourites) }
        )
    }

    private fun resolve(busEireannStops: List<BusEireannStop>, favourites: List<Favourite>): List<BusEireannStop> {
        val busEireannStopsById = busEireannStops.associateBy { it.id }.toMutableMap()
        for (favourite in favourites) {
            val busEireannStop = busEireannStopsById[favourite.id]
            if (busEireannStop != null) {
                // may be deactivated after a user has saved it as a favourite
                busEireannStop.setFavourite()
            }
        }
        return busEireannStopsById.values.toList()
    }

    override fun insert(key: Service, raw: List<BusEireannStop>) {
        val entities = mapper.mapAsList(raw, BusEireannStopEntity::class.java)
        localResource.insertStops(entities)
    }

}

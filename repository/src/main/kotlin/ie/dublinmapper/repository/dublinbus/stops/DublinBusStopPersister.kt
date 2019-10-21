package ie.dublinmapper.repository.dublinbus.stops

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import ie.dublinmapper.datamodel.dublinbus.*
import ie.dublinmapper.datamodel.persister.PersisterDao
import ie.dublinmapper.domain.model.DetailedDublinBusStop
import ie.dublinmapper.domain.model.Favourite
import ie.dublinmapper.repository.AbstractPersister
import ie.dublinmapper.util.InternetManager
import io.reactivex.Maybe
import io.reactivex.functions.BiFunction
import io.rtpi.api.DublinBusStop
import io.rtpi.api.Service
import ma.glasnost.orika.MapperFacade

class DublinBusStopPersister(
    private val localResource: DublinBusStopLocalResource,
    private val mapper: MapperFacade,
    memoryPolicy: MemoryPolicy,
    persisterDao: PersisterDao,
    internetManager: InternetManager
) : AbstractPersister<List<DublinBusStop>, List<DetailedDublinBusStop>, Service>(memoryPolicy, persisterDao, internetManager) {

    override fun select(key: Service): Maybe<List<DetailedDublinBusStop>> {
        return Maybe.zip(
            localResource.selectStops().map { mapper.mapAsList(it, DetailedDublinBusStop::class.java) },
            localResource.selectFavouriteStops().map { mapper.mapAsList(it, Favourite::class.java) },
            BiFunction { dublinBusStops, favourites -> resolve(dublinBusStops, favourites) }
        )
    }

    private fun resolve(dublinBusStops: List<DetailedDublinBusStop>, favourites: List<Favourite>): List<DetailedDublinBusStop> {
        val dublinBusStopsById = dublinBusStops.associateBy { it.id }.toMutableMap()
        for (favourite in favourites) {
            val dublinBusStop = dublinBusStopsById[favourite.id]
            if (dublinBusStop != null) {
                // may be deactivated after a user has saved it as a favourite
                val dublinBusStopWithFavourite = dublinBusStop.copy(favourite = favourite)
                dublinBusStopsById[dublinBusStop.id] = dublinBusStopWithFavourite
            }
        }
        return dublinBusStopsById.values.toList()
    }

    override fun insert(key: Service, raw: List<DublinBusStop>) {
        val entities = mapper.mapAsList(raw, DublinBusStopEntity::class.java)
        localResource.insertStops(entities)
    }

}

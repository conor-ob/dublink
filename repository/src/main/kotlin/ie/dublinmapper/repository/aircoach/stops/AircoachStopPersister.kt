package ie.dublinmapper.repository.aircoach.stops

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import ie.dublinmapper.datamodel.aircoach.*
import ie.dublinmapper.datamodel.persister.PersisterDao
import ie.dublinmapper.domain.model.DetailedAircoachStop
import ie.dublinmapper.domain.model.Favourite
import ie.dublinmapper.repository.AbstractPersister
import ie.dublinmapper.util.InternetManager
import io.reactivex.Maybe
import io.reactivex.functions.BiFunction
import io.rtpi.api.AircoachStop
import io.rtpi.api.Service
import ma.glasnost.orika.MapperFacade

class AircoachStopPersister(
    private val localResource: AircoachStopLocalResource,
    private val mapper: MapperFacade,
    memoryPolicy: MemoryPolicy,
    persisterDao: PersisterDao,
    internetManager: InternetManager
) : AbstractPersister<List<AircoachStop>, List<DetailedAircoachStop>, Service>(memoryPolicy, persisterDao, internetManager) {

    override fun select(key: Service): Maybe<List<DetailedAircoachStop>> {
        return Maybe.zip(
            localResource.selectStops().map { mapper.mapAsList(it, DetailedAircoachStop::class.java) },
            localResource.selectFavouriteStops().map { mapper.mapAsList(it, Favourite::class.java) },
            BiFunction { aircoachStops, favourites -> resolve(aircoachStops, favourites) }
        )
    }

    private fun resolve(aircoachStops: List<DetailedAircoachStop>, favourites: List<Favourite>): List<DetailedAircoachStop> {
        val aircoachStopsById = aircoachStops.associateBy { it.id }.toMutableMap()
        for (favourite in favourites) {
            val aircoachStop = aircoachStopsById[favourite.id]
            if (aircoachStop != null) {
                // stop may be deactivated after a user has saved it as a favourite
                val aircoachStopWithFavourite = aircoachStop.copy(favourite = favourite)
                aircoachStopsById[aircoachStop.id] = aircoachStopWithFavourite
            }
        }
        return aircoachStopsById.values.toList()
    }

    override fun insert(key: Service, raw: List<AircoachStop>) {
        val entities = mapper.mapAsList(raw, AircoachStopEntity::class.java)
        localResource.insertStops(entities)
    }

}

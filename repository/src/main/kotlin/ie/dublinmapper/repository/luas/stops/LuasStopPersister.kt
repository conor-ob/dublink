package ie.dublinmapper.repository.luas.stops

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import ie.dublinmapper.datamodel.LuasStopLocalResource
import ie.dublinmapper.datamodel.ServiceLocationRecordStateLocalResource
import ie.dublinmapper.domain.model.Favourite
import ie.dublinmapper.domain.model.setFavourite
import ie.dublinmapper.repository.AbstractPersister
import ie.dublinmapper.core.InternetManager
import io.reactivex.Observable
import io.rtpi.api.LuasStop
import io.rtpi.api.Service

class LuasStopPersister(
    private val localResource: LuasStopLocalResource,
    memoryPolicy: MemoryPolicy,
    serviceLocationRecordStateLocalResource: ServiceLocationRecordStateLocalResource,
    internetManager: InternetManager
) : AbstractPersister<List<LuasStop>, List<LuasStop>, Service>(memoryPolicy, serviceLocationRecordStateLocalResource, internetManager) {

    override fun select(key: Service): Observable<List<LuasStop>> {
        return localResource.selectStops()
    }

//    override fun select(key: Service): Maybe<List<LuasStop>> {
//        return Maybe.zip(
//            localResource.selectStops().map { mapper.mapAsList(it, LuasStop::class.java) },
//            localResource.selectFavouriteStops().map { mapper.mapAsList(it, Favourite::class.java) },
//            BiFunction { luasStops, favourites -> resolve(luasStops, favourites) }
//        )
//    }

    private fun resolve(luasStops: List<LuasStop>, favourites: List<Favourite>): List<LuasStop> {
        val luasStopsById = luasStops.associateBy { it.id }.toMutableMap()
        for (favourite in favourites) {
            val luasStop = luasStopsById[favourite.id]
            if (luasStop != null) {
                // may be deactivated after a user has saved it as a favourite
                luasStop.setFavourite()
            }
        }
        return luasStopsById.values.toList()
    }

    override fun insert(key: Service, raw: List<LuasStop>) {
        localResource.insertStops(raw)
    }

}

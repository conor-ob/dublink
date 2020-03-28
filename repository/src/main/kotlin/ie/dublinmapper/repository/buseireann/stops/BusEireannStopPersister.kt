package ie.dublinmapper.repository.buseireann.stops

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import ie.dublinmapper.domain.datamodel.BusEireannStopLocalResource
import ie.dublinmapper.domain.datamodel.ServiceLocationRecordStateLocalResource
import ie.dublinmapper.domain.model.Favourite
import ie.dublinmapper.domain.model.setFavourite
import ie.dublinmapper.repository.AbstractPersister
import ie.dublinmapper.domain.service.InternetManager
import io.reactivex.Observable
import io.rtpi.api.BusEireannStop
import io.rtpi.api.Service

class BusEireannStopPersister(
    private val localResource: BusEireannStopLocalResource,
    memoryPolicy: MemoryPolicy,
    serviceLocationRecordStateLocalResource: ServiceLocationRecordStateLocalResource,
    internetManager: InternetManager
) : AbstractPersister<List<BusEireannStop>, List<BusEireannStop>, Service>(memoryPolicy, serviceLocationRecordStateLocalResource, internetManager) {

    override fun select(key: Service): Observable<List<BusEireannStop>> {
        return localResource.selectStops()
    }

//    override fun select(key: Service): Maybe<List<BusEireannStop>> {
//        return Maybe.zip(
//            localResource.selectStops().map { mapper.mapAsList(it, BusEireannStop::class.java) },
//            localResource.selectFavouriteStops().map { mapper.mapAsList(it, Favourite::class.java) },
//            BiFunction { busEireannStops, favourites -> resolve(busEireannStops, favourites) }
//        )
//    }

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
        localResource.insertStops(raw)
    }

}

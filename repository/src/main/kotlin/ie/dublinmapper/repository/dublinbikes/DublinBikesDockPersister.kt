package ie.dublinmapper.repository.dublinbikes

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import ie.dublinmapper.domain.datamodel.DublinBikesDockLocalResource
import ie.dublinmapper.domain.datamodel.ServiceLocationRecordStateLocalResource
import ie.dublinmapper.domain.model.Favourite
import ie.dublinmapper.domain.model.setFavourite
import ie.dublinmapper.domain.service.InternetManager
import ie.dublinmapper.repository.AbstractPersister
import io.reactivex.Observable
import io.rtpi.api.DublinBikesDock
import io.rtpi.api.Service

class DublinBikesDockPersister(
    private val localResource: DublinBikesDockLocalResource,
    memoryPolicy: MemoryPolicy,
    serviceLocationRecordStateLocalResource: ServiceLocationRecordStateLocalResource,
    internetManager: InternetManager
) : AbstractPersister<List<DublinBikesDock>, List<DublinBikesDock>, Service>(memoryPolicy, serviceLocationRecordStateLocalResource, internetManager) {

    override fun select(key: Service): Observable<List<DublinBikesDock>> {
        return localResource.selectDocks()
    }

//    override fun select(key: Service): Maybe<List<DublinBikesDock>> {
//        return Maybe.zip(
//            localResource.selectDocks().map { mapper.mapAsList(it, DublinBikesDock::class.java) },
//            localResource.selectFavouriteDocks().map { mapper.mapAsList(it, Favourite::class.java) },
//            BiFunction { dublinBikesDock, favourites -> resolve(dublinBikesDock, favourites) }
//        )
//    }

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
        localResource.insertDocks(raw)
    }

}

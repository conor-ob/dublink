package ie.dublinmapper.repository.dublinbus.stops

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import ie.dublinmapper.data.dublinbus.DublinBusStopCacheResource
import ie.dublinmapper.data.dublinbus.DublinBusStopEntity
import ie.dublinmapper.data.persister.PersisterDao
import ie.dublinmapper.domain.model.DublinBusStop
import ie.dublinmapper.repository.AbstractPersister
import ie.dublinmapper.service.rtpi.RtpiBusStopInformationJson
import ie.dublinmapper.util.InternetManager
import io.reactivex.Maybe
import ma.glasnost.orika.MapperFacade

class DublinBusStopPersister(
    private val cacheResource: DublinBusStopCacheResource,
    private val mapper: MapperFacade,
    memoryPolicy: MemoryPolicy,
    persisterDao: PersisterDao,
    internetManager: InternetManager
) : AbstractPersister<List<RtpiBusStopInformationJson>, List<DublinBusStop>, String>(memoryPolicy, persisterDao, internetManager) {

    override fun select(key: String): Maybe<List<DublinBusStop>> {
        return cacheResource.selectStops().map { entities ->
            mapper.mapAsList(entities, DublinBusStop::class.java)
        }
    }

    override fun insert(key: String, raw: List<RtpiBusStopInformationJson>) {
        val entities = mapper.mapAsList(raw, DublinBusStopEntity::class.java)
        cacheResource.insertStops(entities)
    }

}

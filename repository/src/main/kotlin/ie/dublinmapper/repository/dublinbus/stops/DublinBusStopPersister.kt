package ie.dublinmapper.repository.dublinbus.stops

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import ie.dublinmapper.data.dublinbus.DublinBusStopCacheResource
import ie.dublinmapper.data.persister.PersisterDao
import ie.dublinmapper.domain.model.DublinBusStop
import ie.dublinmapper.repository.AbstractPersister
import ie.dublinmapper.service.rtpi.RtpiBusStopInformationJson
import io.reactivex.Maybe

class DublinBusStopPersister(
    private val cacheResource: DublinBusStopCacheResource,
    memoryPolicy: MemoryPolicy,
    persisterDao: PersisterDao
) : AbstractPersister<List<RtpiBusStopInformationJson>, List<DublinBusStop>, String>(memoryPolicy, persisterDao) {

    override fun select(key: String): Maybe<List<DublinBusStop>> {
        return cacheResource.selectStops().map { DublinBusStopMapper.mapEntitiesToStops(it) }
    }

    override fun insert(key: String, raw: List<RtpiBusStopInformationJson>) {
        cacheResource.insertStops(DublinBusStopMapper.mapJsonToEntities(raw))
    }

}

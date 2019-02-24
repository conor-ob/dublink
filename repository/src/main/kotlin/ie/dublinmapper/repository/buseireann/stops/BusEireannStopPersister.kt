package ie.dublinmapper.repository.buseireann.stops

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import ie.dublinmapper.data.buseireann.BusEireannStopCacheResource
import ie.dublinmapper.data.persister.PersisterDao
import ie.dublinmapper.domain.model.BusEireannStop
import ie.dublinmapper.repository.AbstractPersister
import ie.dublinmapper.service.rtpi.RtpiBusStopInformationJson
import ie.dublinmapper.util.InternetManager
import io.reactivex.Maybe

class BusEireannStopPersister(
    private val cacheResource: BusEireannStopCacheResource,
    memoryPolicy: MemoryPolicy,
    persisterDao: PersisterDao,
    internetManager: InternetManager
) : AbstractPersister<List<RtpiBusStopInformationJson>, List<BusEireannStop>, String>(memoryPolicy, persisterDao, internetManager) {

    override fun select(key: String): Maybe<List<BusEireannStop>> {
        return cacheResource.selectStops().map { BusEireannStopMapper.mapEntitiesToStops(it) }
    }

    override fun insert(key: String, raw: List<RtpiBusStopInformationJson>) {
        cacheResource.insertStops(BusEireannStopMapper.mapJsonToEntities(raw))
    }

}

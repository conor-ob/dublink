package ie.dublinmapper.repository.luas.stops

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import ie.dublinmapper.data.luas.LuasStopCacheResource
import ie.dublinmapper.data.persister.PersisterDao
import ie.dublinmapper.domain.model.LuasStop
import ie.dublinmapper.repository.AbstractPersister
import ie.dublinmapper.service.rtpi.RtpiBusStopInformationJson
import ie.dublinmapper.util.InternetManager
import io.reactivex.Maybe

class LuasStopPersister(
    private val cacheResource: LuasStopCacheResource,
    memoryPolicy: MemoryPolicy,
    persisterDao: PersisterDao,
    internetManager: InternetManager
) : AbstractPersister<List<RtpiBusStopInformationJson>, List<LuasStop>, String>(memoryPolicy, persisterDao, internetManager) {

    override fun select(key: String): Maybe<List<LuasStop>> {
        return cacheResource.selectStops().map { LuasStopMapper.mapEntitiesToStops(it) }
    }

    override fun insert(key: String, raw: List<RtpiBusStopInformationJson>) {
        cacheResource.insertStops(LuasStopMapper.mapJsonToEntities(raw))
    }

}

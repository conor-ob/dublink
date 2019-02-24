package ie.dublinmapper.repository.swordsexpress.stops

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import ie.dublinmapper.data.persister.PersisterDao
import ie.dublinmapper.data.swordsexpress.SwordsExpressStopCacheResource
import ie.dublinmapper.domain.model.SwordsExpressStop
import ie.dublinmapper.repository.AbstractPersister
import ie.dublinmapper.service.swordsexpress.SwordsExpressStopJson
import ie.dublinmapper.util.InternetManager
import io.reactivex.Maybe

class SwordsExpressStopPersister(
    private val cacheResource: SwordsExpressStopCacheResource,
    memoryPolicy: MemoryPolicy,
    persisterDao: PersisterDao,
    internetManager: InternetManager
) : AbstractPersister<List<SwordsExpressStopJson>, List<SwordsExpressStop>, String>(memoryPolicy, persisterDao, internetManager) {

    override fun select(key: String): Maybe<List<SwordsExpressStop>> {
        return cacheResource.selectStops().map { SwordsExpressStopMapper.mapEntitiesToStops(it) }
    }

    override fun insert(key: String, raw: List<SwordsExpressStopJson>) {
        cacheResource.insertStops(SwordsExpressStopMapper.mapJsonToEntities(raw))
    }

}

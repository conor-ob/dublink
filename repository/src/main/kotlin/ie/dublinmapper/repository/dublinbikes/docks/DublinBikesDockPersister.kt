package ie.dublinmapper.repository.dublinbikes.docks

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import ie.dublinmapper.data.dublinbikes.DublinBikesDockCacheResource
import ie.dublinmapper.data.persister.PersisterDao
import ie.dublinmapper.domain.model.DublinBikesDock
import ie.dublinmapper.repository.AbstractPersister
import ie.dublinmapper.service.jcdecaux.StationJson
import ie.dublinmapper.util.InternetManager
import io.reactivex.Maybe

class DublinBikesDockPersister(
    private val cacheResource: DublinBikesDockCacheResource,
    memoryPolicy: MemoryPolicy,
    persisterDao: PersisterDao,
    internetManager: InternetManager
) : AbstractPersister<List<StationJson>, List<DublinBikesDock>, String>(memoryPolicy, persisterDao, internetManager) {

    override fun select(key: String): Maybe<List<DublinBikesDock>> {
        return cacheResource.selectDocks().map { DublinBikesDockMapper.mapEntitiesToStops(it) }
    }

    override fun insert(key: String, raw: List<StationJson>) {
        cacheResource.insertDocks(DublinBikesDockMapper.mapJsonToEntities(raw))
    }

}

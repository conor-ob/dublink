package ie.dublinmapper.repository.swordsexpress.stops

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import ie.dublinmapper.datamodel.persister.PersisterDao
import ie.dublinmapper.datamodel.swordsexpress.SwordsExpressStopLocalResource
import ie.dublinmapper.domain.model.SwordsExpressStop
import ie.dublinmapper.repository.AbstractPersister
import ie.dublinmapper.service.swordsexpress.SwordsExpressStopJson
import ie.dublinmapper.util.InternetManager
import ie.dublinmapper.util.Service
import io.reactivex.Maybe

class SwordsExpressStopPersister(
    private val localResource: SwordsExpressStopLocalResource,
    memoryPolicy: MemoryPolicy,
    persisterDao: PersisterDao,
    internetManager: InternetManager
) : AbstractPersister<List<SwordsExpressStopJson>, List<SwordsExpressStop>, Service>(memoryPolicy, persisterDao, internetManager) {

    override fun select(key: Service): Maybe<List<SwordsExpressStop>> {
        return localResource.selectStops().map { SwordsExpressStopMapper.mapEntitiesToStops(it) }
    }

    override fun insert(key: Service, raw: List<SwordsExpressStopJson>) {
        localResource.insertStops(SwordsExpressStopMapper.mapJsonToEntities(raw))
    }

}

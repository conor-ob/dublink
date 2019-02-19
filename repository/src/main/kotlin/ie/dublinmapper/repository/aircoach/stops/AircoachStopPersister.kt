package ie.dublinmapper.repository.aircoach.stops

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import ie.dublinmapper.data.aircoach.AircoachStopCacheResource
import ie.dublinmapper.data.persister.PersisterDao
import ie.dublinmapper.domain.model.AircoachStop
import ie.dublinmapper.repository.AbstractPersister
import ie.dublinmapper.service.aircoach.AircoachStopJson
import io.reactivex.Maybe

class AircoachStopPersister(
    private val cacheResource: AircoachStopCacheResource,
    memoryPolicy: MemoryPolicy,
    persisterDao: PersisterDao
) : AbstractPersister<List<AircoachStopJson>, List<AircoachStop>, String>(memoryPolicy, persisterDao) {

    override fun select(key: String): Maybe<List<AircoachStop>> {
        return cacheResource.selectStops().map { AircoachStopMapper.mapEntitiesToStops(it) }
    }

    override fun insert(key: String, raw: List<AircoachStopJson>) {
        cacheResource.insertStops(AircoachStopMapper.mapJsonToEntities(raw))
    }

}

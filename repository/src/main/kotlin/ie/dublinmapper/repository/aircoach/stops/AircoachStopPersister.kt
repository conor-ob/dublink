package ie.dublinmapper.repository.aircoach.stops

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import ie.dublinmapper.datamodel.aircoach.AircoachStopCacheResource
import ie.dublinmapper.datamodel.aircoach.AircoachStopEntity
import ie.dublinmapper.datamodel.persister.PersisterDao
import ie.dublinmapper.domain.model.AircoachStop
import ie.dublinmapper.repository.AbstractPersister
import ie.dublinmapper.service.aircoach.AircoachStopJson
import ie.dublinmapper.util.InternetManager
import io.reactivex.Maybe
import ma.glasnost.orika.MapperFacade

class AircoachStopPersister(
    private val cacheResource: AircoachStopCacheResource,
    private val mapper: MapperFacade,
    memoryPolicy: MemoryPolicy,
    persisterDao: PersisterDao,
    internetManager: InternetManager
) : AbstractPersister<List<AircoachStopJson>, List<AircoachStop>, String>(memoryPolicy, persisterDao, internetManager) {

    override fun select(key: String): Maybe<List<AircoachStop>> {
        return cacheResource.selectStops().map { entities ->
            mapper.mapAsList(entities, AircoachStop::class.java)
        }
    }

    override fun insert(key: String, raw: List<AircoachStopJson>) {
        val entities = mapper.mapAsList(raw, AircoachStopEntity::class.java)
        cacheResource.insertStops(entities)
    }

}

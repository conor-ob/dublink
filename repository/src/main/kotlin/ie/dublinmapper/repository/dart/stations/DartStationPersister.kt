package ie.dublinmapper.repository.dart.stations

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import ie.dublinmapper.data.dart.DartStationCacheResource
import ie.dublinmapper.data.dart.DartStationEntity
import ie.dublinmapper.data.persister.PersisterDao
import ie.dublinmapper.domain.model.DartStation
import ie.dublinmapper.repository.AbstractPersister
import ie.dublinmapper.service.irishrail.IrishRailStationXml
import ie.dublinmapper.util.InternetManager
import io.reactivex.Maybe
import ma.glasnost.orika.MapperFacade

class DartStationPersister(
    private val cacheResource: DartStationCacheResource,
    private val mapper: MapperFacade,
    memoryPolicy: MemoryPolicy,
    persisterDao: PersisterDao,
    internetManager: InternetManager
) : AbstractPersister<List<IrishRailStationXml>, List<DartStation>, String>(memoryPolicy, persisterDao, internetManager) {

    override fun select(key: String): Maybe<List<DartStation>> {
        return cacheResource.selectStops().map { entities ->
            mapper.mapAsList(entities, DartStation::class.java)
        }
    }

    override fun insert(key: String, raw: List<IrishRailStationXml>) {
        val entities = mapper.mapAsList(raw, DartStationEntity::class.java)
        cacheResource.insertStops(entities)
    }

}

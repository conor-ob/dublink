package ie.dublinmapper.repository.dart.stations

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import ie.dublinmapper.datamodel.dart.DartStationCacheResource
import ie.dublinmapper.datamodel.dart.DartStationEntity
import ie.dublinmapper.datamodel.persister.PersisterDao
import ie.dublinmapper.domain.model.DartStation
import ie.dublinmapper.repository.AbstractPersister
import ie.dublinmapper.service.irishrail.IrishRailStationXml
import ie.dublinmapper.util.InternetManager
import ie.dublinmapper.util.Service
import io.reactivex.Maybe
import ma.glasnost.orika.MapperFacade

class DartStationPersister(
    private val cacheResource: DartStationCacheResource,
    private val mapper: MapperFacade,
    memoryPolicy: MemoryPolicy,
    persisterDao: PersisterDao,
    internetManager: InternetManager
) : AbstractPersister<List<IrishRailStationXml>, List<DartStation>, Service>(memoryPolicy, persisterDao, internetManager) {

    override fun select(key: Service): Maybe<List<DartStation>> {
        return cacheResource.selectStops().map { entities ->
            mapper.mapAsList(entities, DartStation::class.java)
        }
    }

    override fun insert(key: Service, raw: List<IrishRailStationXml>) {
        val entities = mapper.mapAsList(raw, DartStationEntity::class.java)
        cacheResource.insertStops(entities)
    }

}

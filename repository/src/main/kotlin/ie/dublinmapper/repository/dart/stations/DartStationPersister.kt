package ie.dublinmapper.repository.dart.stations

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import ie.dublinmapper.data.dart.DartStationCacheResource
import ie.dublinmapper.data.persister.PersisterDao
import ie.dublinmapper.domain.model.DartStation
import ie.dublinmapper.repository.AbstractPersister
import ie.dublinmapper.service.irishrail.IrishRailStationXml
import ie.dublinmapper.util.InternetManager
import io.reactivex.Maybe

class DartStationPersister(
    private val cacheResource: DartStationCacheResource,
    memoryPolicy: MemoryPolicy,
    persisterDao: PersisterDao,
    internetManager: InternetManager
) : AbstractPersister<List<IrishRailStationXml>, List<DartStation>, String>(memoryPolicy, persisterDao, internetManager) {

    override fun select(key: String): Maybe<List<DartStation>> {
        return cacheResource.selectStops().map { DartStationMapper.mapEntitiesToStations(it) }
    }

    override fun insert(key: String, raw: List<IrishRailStationXml>) {
        cacheResource.insertStops(DartStationMapper.mapJsonToEntities(raw))
    }

}

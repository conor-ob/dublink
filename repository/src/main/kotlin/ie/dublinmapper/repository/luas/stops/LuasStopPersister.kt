package ie.dublinmapper.repository.luas.stops

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import ie.dublinmapper.data.luas.LuasStopCacheResource
import ie.dublinmapper.data.luas.LuasStopEntity
import ie.dublinmapper.data.persister.PersisterDao
import ie.dublinmapper.domain.model.LuasStop
import ie.dublinmapper.repository.AbstractPersister
import ie.dublinmapper.service.rtpi.RtpiBusStopInformationJson
import ie.dublinmapper.util.InternetManager
import io.reactivex.Maybe
import ma.glasnost.orika.MapperFacade

class LuasStopPersister(
    private val cacheResource: LuasStopCacheResource,
    private val mapper: MapperFacade,
    memoryPolicy: MemoryPolicy,
    persisterDao: PersisterDao,
    internetManager: InternetManager
) : AbstractPersister<List<RtpiBusStopInformationJson>, List<LuasStop>, String>(memoryPolicy, persisterDao, internetManager) {

    override fun select(key: String): Maybe<List<LuasStop>> {
        return cacheResource.selectStops().map { entities ->
            mapper.mapAsList(entities, LuasStop::class.java)
        }
    }

    override fun insert(key: String, raw: List<RtpiBusStopInformationJson>) {
        val entities = mapper.mapAsList(raw, LuasStopEntity::class.java)
        cacheResource.insertStops(entities)
    }

}

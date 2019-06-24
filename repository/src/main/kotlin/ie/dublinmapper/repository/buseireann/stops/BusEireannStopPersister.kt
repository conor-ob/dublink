package ie.dublinmapper.repository.buseireann.stops

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import ie.dublinmapper.datamodel.buseireann.BusEireannStopCacheResource
import ie.dublinmapper.datamodel.buseireann.BusEireannStopEntity
import ie.dublinmapper.datamodel.persister.PersisterDao
import ie.dublinmapper.domain.model.BusEireannStop
import ie.dublinmapper.repository.AbstractPersister
import ie.dublinmapper.service.rtpi.RtpiBusStopInformationJson
import ie.dublinmapper.util.InternetManager
import ie.dublinmapper.util.Service
import io.reactivex.Maybe
import ma.glasnost.orika.MapperFacade

class BusEireannStopPersister(
    private val cacheResource: BusEireannStopCacheResource,
    private val mapper: MapperFacade,
    memoryPolicy: MemoryPolicy,
    persisterDao: PersisterDao,
    internetManager: InternetManager
) : AbstractPersister<List<RtpiBusStopInformationJson>, List<BusEireannStop>, Service>(memoryPolicy, persisterDao, internetManager) {

    override fun select(key: Service): Maybe<List<BusEireannStop>> {
        return cacheResource.selectStops().map { entities->
            mapper.mapAsList(entities, BusEireannStop::class.java)
        }
    }

    override fun insert(key: Service, raw: List<RtpiBusStopInformationJson>) {
        val entities = mapper.mapAsList(raw, BusEireannStopEntity::class.java)
        cacheResource.insertStops(entities)
    }

}

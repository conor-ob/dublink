package ie.dublinmapper.repository.aircoach.stops

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import ie.dublinmapper.datamodel.aircoach.*
import ie.dublinmapper.datamodel.persister.ServiceLocationRecordStateLocalResource
import ie.dublinmapper.repository.AbstractPersister
import ie.dublinmapper.util.InternetManager
import io.reactivex.Observable
import io.rtpi.api.AircoachStop
import io.rtpi.api.Service
import ma.glasnost.orika.MapperFacade

class AircoachStopPersister(
    private val localResource: AircoachStopLocalResource,
    private val mapper: MapperFacade,
    memoryPolicy: MemoryPolicy,
    serviceLocationRecordStateLocalResource: ServiceLocationRecordStateLocalResource,
    internetManager: InternetManager
) : AbstractPersister<List<AircoachStop>, List<AircoachStop>, Service>(memoryPolicy, serviceLocationRecordStateLocalResource, internetManager) {

    override fun select(key: Service): Observable<List<AircoachStop>> {
        return localResource.selectStops()
    }

    override fun insert(key: Service, raw: List<AircoachStop>) {
        localResource.insertStops(raw)
    }

}

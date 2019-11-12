package ie.dublinmapper.repository.dublinbus.stops

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import ie.dublinmapper.datamodel.dublinbus.*
import ie.dublinmapper.datamodel.persister.ServiceLocationRecordStateLocalResource
import ie.dublinmapper.repository.AbstractPersister
import ie.dublinmapper.util.InternetManager
import io.reactivex.Observable
import io.rtpi.api.DublinBusStop
import io.rtpi.api.Service

class DublinBusStopPersister(
    private val localResource: DublinBusStopLocalResource,
    memoryPolicy: MemoryPolicy,
    serviceLocationRecordStateLocalResource: ServiceLocationRecordStateLocalResource,
    internetManager: InternetManager
) : AbstractPersister<List<DublinBusStop>, List<DublinBusStop>, Service>(memoryPolicy, serviceLocationRecordStateLocalResource, internetManager) {

    override fun select(key: Service): Observable<List<DublinBusStop>> {
        return localResource.selectStops()
    }

    override fun insert(key: Service, raw: List<DublinBusStop>) {
        localResource.insertStops(raw)
    }

}

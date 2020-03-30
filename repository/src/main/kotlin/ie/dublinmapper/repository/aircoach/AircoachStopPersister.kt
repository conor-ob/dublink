package ie.dublinmapper.repository.aircoach

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import ie.dublinmapper.domain.datamodel.AircoachStopLocalResource
import ie.dublinmapper.domain.datamodel.ServiceLocationRecordStateLocalResource
import ie.dublinmapper.domain.service.InternetManager
import ie.dublinmapper.repository.AbstractPersister
import io.reactivex.Observable
import io.rtpi.api.AircoachStop
import io.rtpi.api.Service

class AircoachStopPersister(
    private val localResource: AircoachStopLocalResource,
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
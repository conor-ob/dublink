package ie.dublinmapper.repository.irishrail.stations

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import ie.dublinmapper.domain.datamodel.IrishRailStationLocalResource
import ie.dublinmapper.domain.datamodel.ServiceLocationRecordStateLocalResource
import ie.dublinmapper.repository.AbstractPersister
import ie.dublinmapper.core.InternetManager
import io.reactivex.Observable
import io.rtpi.api.IrishRailStation
import io.rtpi.api.Service

class IrishRailStationPersister(
    private val localResource: IrishRailStationLocalResource,
    memoryPolicy: MemoryPolicy,
    serviceLocationRecordStateLocalResource: ServiceLocationRecordStateLocalResource,
    internetManager: InternetManager
) : AbstractPersister<List<IrishRailStation>, List<IrishRailStation>, Service>(memoryPolicy, serviceLocationRecordStateLocalResource, internetManager) {

    override fun select(key: Service): Observable<List<IrishRailStation>> {
        return localResource.selectStations()
    }

    override fun insert(key: Service, raw: List<IrishRailStation>) {
        localResource.insertStations(raw)
    }

}

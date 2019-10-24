package ie.dublinmapper.repository.dublinbus.stops

import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import ie.dublinmapper.repository.ServiceLocationRepository
import ie.dublinmapper.util.EnabledServiceManager
import io.rtpi.api.DublinBusStop
import io.rtpi.api.Service

class DublinBusStopRepository(
    serviceLocationStore: StoreRoom<List<DublinBusStop>, Service>,
    enabledServiceManager: EnabledServiceManager
) : ServiceLocationRepository<DublinBusStop>(
    service = Service.DUBLIN_BUS,
    serviceLocationStore = serviceLocationStore,
    enabledServiceManager = enabledServiceManager
)

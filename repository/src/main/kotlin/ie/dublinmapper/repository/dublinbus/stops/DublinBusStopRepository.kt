package ie.dublinmapper.repository.dublinbus.stops

import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import ie.dublinmapper.domain.model.DetailedDublinBusStop
import ie.dublinmapper.repository.ServiceLocationRepository
import ie.dublinmapper.util.EnabledServiceManager
import io.rtpi.api.Service

class DublinBusStopRepository(
    serviceLocationStore: StoreRoom<List<DetailedDublinBusStop>, Service>,
    enabledServiceManager: EnabledServiceManager
) : ServiceLocationRepository<DetailedDublinBusStop>(
    service = Service.DUBLIN_BUS,
    serviceLocationStore = serviceLocationStore,
    enabledServiceManager = enabledServiceManager
)

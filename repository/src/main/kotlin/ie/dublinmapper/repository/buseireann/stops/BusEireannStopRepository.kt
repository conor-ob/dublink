package ie.dublinmapper.repository.buseireann.stops

import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import ie.dublinmapper.repository.ServiceLocationRepository
import ie.dublinmapper.domain.service.EnabledServiceManager
import io.rtpi.api.BusEireannStop
import io.rtpi.api.Service

class BusEireannStopRepository(
    serviceLocationStore: StoreRoom<List<BusEireannStop>, Service>,
    enabledServiceManager: EnabledServiceManager
) : ServiceLocationRepository<BusEireannStop>(
    service = Service.BUS_EIREANN,
    serviceLocationStore = serviceLocationStore,
    enabledServiceManager = enabledServiceManager
)

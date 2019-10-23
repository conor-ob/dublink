package ie.dublinmapper.repository.buseireann.stops

import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import ie.dublinmapper.domain.model.DetailedBusEireannStop
import ie.dublinmapper.repository.ServiceLocationRepository
import ie.dublinmapper.util.EnabledServiceManager
import io.rtpi.api.Service

class BusEireannStopRepository(
    serviceLocationStore: StoreRoom<List<DetailedBusEireannStop>, Service>,
    enabledServiceManager: EnabledServiceManager
) : ServiceLocationRepository<DetailedBusEireannStop>(
    service = Service.BUS_EIREANN,
    serviceLocationStore = serviceLocationStore,
    enabledServiceManager = enabledServiceManager
)

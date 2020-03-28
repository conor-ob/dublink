package ie.dublinmapper.repository.aircoach.stops

import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import ie.dublinmapper.repository.ServiceLocationRepository
import ie.dublinmapper.domain.service.EnabledServiceManager
import io.rtpi.api.AircoachStop
import io.rtpi.api.Service

class AircoachStopRepository(
    serviceLocationStore: StoreRoom<List<AircoachStop>, Service>,
    enabledServiceManager: EnabledServiceManager
) : ServiceLocationRepository<AircoachStop>(
    service = Service.AIRCOACH,
    serviceLocationStore = serviceLocationStore,
    enabledServiceManager = enabledServiceManager
)

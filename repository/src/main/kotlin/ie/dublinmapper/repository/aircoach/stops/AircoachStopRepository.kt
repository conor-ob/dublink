package ie.dublinmapper.repository.aircoach.stops

import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import ie.dublinmapper.domain.model.DetailedAircoachStop
import ie.dublinmapper.repository.ServiceLocationRepository
import ie.dublinmapper.util.EnabledServiceManager
import io.rtpi.api.Service

class AircoachStopRepository(
    serviceLocationStore: StoreRoom<List<DetailedAircoachStop>, Service>,
    enabledServiceManager: EnabledServiceManager
) : ServiceLocationRepository<DetailedAircoachStop>(
    service = Service.AIRCOACH,
    serviceLocationStore = serviceLocationStore,
    enabledServiceManager = enabledServiceManager
)

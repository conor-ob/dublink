package ie.dublinmapper.repository.irishrail.stations

import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import ie.dublinmapper.repository.ServiceLocationRepository
import ie.dublinmapper.domain.service.EnabledServiceManager
import io.rtpi.api.IrishRailStation
import io.rtpi.api.Service

class IrishRailStationRepository(
    serviceLocationStore: StoreRoom<List<IrishRailStation>, Service>,
    enabledServiceManager: EnabledServiceManager
) : ServiceLocationRepository<IrishRailStation>(
    service = Service.IRISH_RAIL,
    serviceLocationStore = serviceLocationStore,
    enabledServiceManager = enabledServiceManager
)

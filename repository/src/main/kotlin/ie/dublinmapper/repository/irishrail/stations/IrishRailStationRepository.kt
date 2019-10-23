package ie.dublinmapper.repository.irishrail.stations

import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import ie.dublinmapper.domain.model.DetailedIrishRailStation
import ie.dublinmapper.repository.ServiceLocationRepository
import ie.dublinmapper.util.EnabledServiceManager
import io.rtpi.api.Service

class IrishRailStationRepository(
    serviceLocationStore: StoreRoom<List<DetailedIrishRailStation>, Service>,
    enabledServiceManager: EnabledServiceManager
) : ServiceLocationRepository<DetailedIrishRailStation>(
    service = Service.IRISH_RAIL,
    serviceLocationStore = serviceLocationStore,
    enabledServiceManager = enabledServiceManager
)

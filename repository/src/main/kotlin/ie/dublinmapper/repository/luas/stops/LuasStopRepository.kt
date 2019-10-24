package ie.dublinmapper.repository.luas.stops

import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import ie.dublinmapper.repository.ServiceLocationRepository
import ie.dublinmapper.util.EnabledServiceManager
import io.rtpi.api.LuasStop
import io.rtpi.api.Service

class LuasStopRepository(
    serviceLocationStore: StoreRoom<List<LuasStop>, Service>,
    enabledServiceManager: EnabledServiceManager
) : ServiceLocationRepository<LuasStop>(
    service = Service.LUAS,
    serviceLocationStore = serviceLocationStore,
    enabledServiceManager = enabledServiceManager
)

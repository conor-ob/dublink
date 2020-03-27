package ie.dublinmapper.repository.dublinbikes.docks

import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import ie.dublinmapper.repository.ServiceLocationRepository
import ie.dublinmapper.core.EnabledServiceManager
import io.rtpi.api.DublinBikesDock
import io.rtpi.api.Service

class DublinBikesDockRepository(
    serviceLocationStore: StoreRoom<List<DublinBikesDock>, Service>,
    enabledServiceManager: EnabledServiceManager
) : ServiceLocationRepository<DublinBikesDock>(
    service = Service.DUBLIN_BIKES,
    serviceLocationStore = serviceLocationStore,
    enabledServiceManager = enabledServiceManager
)

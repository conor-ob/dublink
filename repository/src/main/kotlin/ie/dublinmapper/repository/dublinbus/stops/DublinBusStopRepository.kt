package ie.dublinmapper.repository.dublinbus.stops

import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import ie.dublinmapper.domain.model.DublinBusStop
import ie.dublinmapper.repository.ServiceLocationRepository

class DublinBusStopRepository(
    store: StoreRoom<List<DublinBusStop>, String>
) : ServiceLocationRepository<DublinBusStop>(key = "dublinbus_stops", store = store)

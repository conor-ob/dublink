package ie.dublinmapper.repository.dublinbus.stops

import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import ie.dublinmapper.domain.model.DublinBusStop
import ie.dublinmapper.domain.model.LuasStop
import ie.dublinmapper.domain.repository.FavouriteRepository
import ie.dublinmapper.repository.ServiceLocationRepository
import ie.dublinmapper.util.Service

class DublinBusStopRepository(
    serviceLocationStore: StoreRoom<List<DublinBusStop>, Service>
) : ServiceLocationRepository<DublinBusStop>(
    service = Service.DUBLIN_BUS,
    serviceLocationStore = serviceLocationStore
)

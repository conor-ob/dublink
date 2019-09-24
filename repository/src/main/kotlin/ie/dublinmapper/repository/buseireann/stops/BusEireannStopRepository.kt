package ie.dublinmapper.repository.buseireann.stops

import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import ie.dublinmapper.domain.model.BusEireannStop
import ie.dublinmapper.domain.model.LuasStop
import ie.dublinmapper.domain.repository.FavouriteRepository
import ie.dublinmapper.repository.ServiceLocationRepository
import ie.dublinmapper.util.Service

class BusEireannStopRepository(
    serviceLocationStore: StoreRoom<List<BusEireannStop>, Service>
) : ServiceLocationRepository<BusEireannStop>(
    service = Service.BUS_EIREANN,
    serviceLocationStore = serviceLocationStore
)

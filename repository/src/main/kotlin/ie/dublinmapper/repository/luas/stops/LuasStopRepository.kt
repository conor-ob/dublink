package ie.dublinmapper.repository.luas.stops

import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import ie.dublinmapper.domain.model.DetailedLuasStop
import ie.dublinmapper.repository.ServiceLocationRepository
import io.rtpi.api.Service

class LuasStopRepository(
    serviceLocationStore: StoreRoom<List<DetailedLuasStop>, Service>
) : ServiceLocationRepository<DetailedLuasStop>(
    service = Service.LUAS,
    serviceLocationStore = serviceLocationStore
)

package ie.dublinmapper.repository.luas.stops

import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import ie.dublinmapper.domain.model.LuasStop
import ie.dublinmapper.repository.ServiceLocationRepository

class LuasStopRepository(
    store: StoreRoom<List<LuasStop>, String>
) : ServiceLocationRepository<LuasStop>(key = "luas_stops", store = store)

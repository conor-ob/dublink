package ie.dublinmapper.repository.luas.stops

import com.nytimes.android.external.store3.base.impl.Store
import ie.dublinmapper.domain.model.LuasStop
import ie.dublinmapper.repository.ServiceLocationRepository

class LuasStopRepository(
    store: Store<List<LuasStop>, String>
) : ServiceLocationRepository<LuasStop>(store) {

    override fun key() = "luas_stops"

}

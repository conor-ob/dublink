package ie.dublinmapper.repository.dublinbus

import com.nytimes.android.external.store3.base.impl.Store
import ie.dublinmapper.domain.model.DublinBusStop
import ie.dublinmapper.repository.ServiceLocationRepository

class DublinBusStopRepository(
    store: Store<List<DublinBusStop>, String>
) : ServiceLocationRepository<DublinBusStop>(store) {

    override fun key() = "dublin_bus_stops"

}

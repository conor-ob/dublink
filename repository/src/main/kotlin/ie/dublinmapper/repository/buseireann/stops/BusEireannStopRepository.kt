package ie.dublinmapper.repository.buseireann.stops

import com.nytimes.android.external.store3.base.impl.Store
import ie.dublinmapper.domain.model.BusEireannStop
import ie.dublinmapper.repository.ServiceLocationRepository

class BusEireannStopRepository(
    store: Store<List<BusEireannStop>, String>
) : ServiceLocationRepository<BusEireannStop>(store) {

    override fun key() = "bus_eireann_stops"

}

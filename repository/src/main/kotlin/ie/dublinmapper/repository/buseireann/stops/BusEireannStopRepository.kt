package ie.dublinmapper.repository.buseireann.stops

import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import ie.dublinmapper.domain.model.BusEireannStop
import ie.dublinmapper.repository.ServiceLocationRepository

class BusEireannStopRepository(
    store: StoreRoom<List<BusEireannStop>, String>
) : ServiceLocationRepository<BusEireannStop>(store) {

    override fun key() = "buseireann_stops"

}

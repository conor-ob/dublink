package ie.dublinmapper.repository.buseireann.stops

import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import ie.dublinmapper.domain.model.BusEireannStop
import ie.dublinmapper.repository.ServiceLocationRoomRepository

class BusEireannRoomRepository(
    store: StoreRoom<List<BusEireannStop>, String>
) : ServiceLocationRoomRepository<BusEireannStop>(store) {

    override fun key() = "buseireann_stops"

}

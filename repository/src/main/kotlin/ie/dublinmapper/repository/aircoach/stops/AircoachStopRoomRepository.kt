package ie.dublinmapper.repository.aircoach.stops

import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import ie.dublinmapper.domain.model.AircoachStop
import ie.dublinmapper.repository.ServiceLocationRoomRepository

class AircoachStopRoomRepository(
    store: StoreRoom<List<AircoachStop>, String>
) : ServiceLocationRoomRepository<AircoachStop>(store) {

    override fun key() = "aircoach_stops"

}

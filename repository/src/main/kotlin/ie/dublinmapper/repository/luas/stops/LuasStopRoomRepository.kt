package ie.dublinmapper.repository.luas.stops

import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import ie.dublinmapper.domain.model.LuasStop
import ie.dublinmapper.repository.ServiceLocationRoomRepository

class LuasStopRoomRepository(
    store: StoreRoom<List<LuasStop>, String>
) : ServiceLocationRoomRepository<LuasStop>(store) {

    override fun key() = "luas_stops"

}

package ie.dublinmapper.repository.dart.stations

import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import ie.dublinmapper.domain.model.DartStation
import ie.dublinmapper.repository.ServiceLocationRoomRepository

class DartStationRoomRepository(
    store: StoreRoom<List<DartStation>, String>
) : ServiceLocationRoomRepository<DartStation>(store) {

    override fun key() = "dart_stations"

}
package ie.dublinmapper.repository.dublinbikes.docks

import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import ie.dublinmapper.domain.model.DublinBikesDock
import ie.dublinmapper.repository.ServiceLocationRoomRepository

class DublinBikesDockRoomRepository(
    store: StoreRoom<List<DublinBikesDock>, String>
) : ServiceLocationRoomRepository<DublinBikesDock>(store) {

    //TODO 2 min cache expiry
    override fun key() = "dublinbikes_docks"

}

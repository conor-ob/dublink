package ie.dublinmapper.repository.dublinbikes.docks

import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import ie.dublinmapper.domain.model.DublinBikesDock
import ie.dublinmapper.repository.ServiceLocationRepository

class DublinBikesDockRepository(
    store: StoreRoom<List<DublinBikesDock>, String>
) : ServiceLocationRepository<DublinBikesDock>(store) {

    //TODO 2 min cache expiry
    override fun key() = "dublinbikes_docks"

}

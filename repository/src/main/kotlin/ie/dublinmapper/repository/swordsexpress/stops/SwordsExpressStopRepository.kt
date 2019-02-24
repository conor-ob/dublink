package ie.dublinmapper.repository.swordsexpress.stops

import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import ie.dublinmapper.domain.model.SwordsExpressStop
import ie.dublinmapper.repository.ServiceLocationRepository

class SwordsExpressStopRepository(
    store: StoreRoom<List<SwordsExpressStop>, String>
) : ServiceLocationRepository<SwordsExpressStop>(store) {

    override fun key() = "swordsexpress_stops"

}

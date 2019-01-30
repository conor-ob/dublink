package ie.dublinmapper.repository.swordsexpress

import com.nytimes.android.external.store3.base.impl.Store
import ie.dublinmapper.domain.model.SwordsExpressStop
import ie.dublinmapper.repository.ServiceLocationRepository

class SwordsExpressStopRepository(
    store: Store<List<SwordsExpressStop>, String>
) : ServiceLocationRepository<SwordsExpressStop>(store) {

    override fun key() = "swords_express_stops"

}

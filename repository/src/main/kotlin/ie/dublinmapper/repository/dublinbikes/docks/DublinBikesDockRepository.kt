package ie.dublinmapper.repository.dublinbikes.docks

import com.nytimes.android.external.store3.base.impl.Store
import ie.dublinmapper.domain.model.DublinBikesDock
import ie.dublinmapper.repository.ServiceLocationRepository

class DublinBikesDockRepository(
    store: Store<List<DublinBikesDock>, String>
) : ServiceLocationRepository<DublinBikesDock>(store) {

    override fun key() = "dublin_bikes_docks"

}

package ie.dublinmapper.repository.aircoach

import com.nytimes.android.external.store3.base.impl.Store
import ie.dublinmapper.domain.model.AircoachStop
import ie.dublinmapper.repository.ServiceLocationRepository

class AircoachStopRepository(
    store: Store<List<AircoachStop>, String>
) : ServiceLocationRepository<AircoachStop>(store) {

    override fun key() = "aircoach_stops"

}
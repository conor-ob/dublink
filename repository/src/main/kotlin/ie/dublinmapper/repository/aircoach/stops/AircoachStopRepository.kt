package ie.dublinmapper.repository.aircoach.stops

import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import ie.dublinmapper.domain.model.AircoachStop
import ie.dublinmapper.repository.ServiceLocationRepository

class AircoachStopRepository(
    store: StoreRoom<List<AircoachStop>, String>
) : ServiceLocationRepository<AircoachStop>(store) {

    override fun key() = "aircoach_stops"

}

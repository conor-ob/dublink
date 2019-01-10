package ie.dublinmapper.repository.dart

import com.nytimes.android.external.store3.base.impl.Store
import ie.dublinmapper.domain.model.DartStation
import ie.dublinmapper.repository.ServiceLocationRepository

class DartStationRepository(
    store: Store<List<DartStation>, String>
) : ServiceLocationRepository<DartStation>(store) {

    override fun key() = "dart_stations"

}
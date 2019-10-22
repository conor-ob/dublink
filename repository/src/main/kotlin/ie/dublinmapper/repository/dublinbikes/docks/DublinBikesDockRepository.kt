package ie.dublinmapper.repository.dublinbikes.docks

import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import ie.dublinmapper.domain.model.DetailedDublinBikesDock
import ie.dublinmapper.repository.ServiceLocationRepository
import io.rtpi.api.Service

class DublinBikesDockRepository(
    serviceLocationStore: StoreRoom<List<DetailedDublinBikesDock>, Service>
) : ServiceLocationRepository<DetailedDublinBikesDock>(
    service = Service.DUBLIN_BIKES,
    serviceLocationStore = serviceLocationStore
)

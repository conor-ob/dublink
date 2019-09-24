package ie.dublinmapper.repository.dublinbikes.docks

import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import ie.dublinmapper.domain.model.DublinBikesDock
import ie.dublinmapper.domain.model.LuasStop
import ie.dublinmapper.domain.repository.FavouriteRepository
import ie.dublinmapper.repository.ServiceLocationRepository
import ie.dublinmapper.util.Service

class DublinBikesDockRepository(
    serviceLocationStore: StoreRoom<List<DublinBikesDock>, Service>
) : ServiceLocationRepository<DublinBikesDock>(
    service = Service.DUBLIN_BIKES,
    serviceLocationStore = serviceLocationStore
)

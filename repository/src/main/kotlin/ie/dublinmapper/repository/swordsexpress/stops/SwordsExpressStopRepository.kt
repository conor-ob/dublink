package ie.dublinmapper.repository.swordsexpress.stops

import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import ie.dublinmapper.domain.model.LuasStop
import ie.dublinmapper.domain.model.SwordsExpressStop
import ie.dublinmapper.domain.repository.FavouriteRepository
import ie.dublinmapper.repository.ServiceLocationRepository
import ie.dublinmapper.util.Service

class SwordsExpressStopRepository(
    serviceLocationStore: StoreRoom<List<SwordsExpressStop>, Service>
) : ServiceLocationRepository<SwordsExpressStop>(
    service = Service.SWORDS_EXPRESS,
    serviceLocationStore = serviceLocationStore
)
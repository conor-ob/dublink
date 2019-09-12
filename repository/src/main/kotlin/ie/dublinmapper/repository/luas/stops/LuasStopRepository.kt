package ie.dublinmapper.repository.luas.stops

import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import ie.dublinmapper.domain.model.LuasStop
import ie.dublinmapper.domain.repository.FavouriteRepository
import ie.dublinmapper.repository.ServiceLocationRepository
import ie.dublinmapper.util.Service

class LuasStopRepository(
    serviceLocationStore: StoreRoom<List<LuasStop>, Service>,
    favouriteRepository: FavouriteRepository
) : ServiceLocationRepository<LuasStop>(
    service = Service.LUAS,
    serviceLocationStore = serviceLocationStore,
    favouriteRepository = favouriteRepository
)
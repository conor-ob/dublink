package ie.dublinmapper.repository.dart.stations

import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import ie.dublinmapper.domain.model.DartStation
import ie.dublinmapper.domain.model.LuasStop
import ie.dublinmapper.domain.repository.FavouriteRepository
import ie.dublinmapper.repository.ServiceLocationRepository
import ie.dublinmapper.util.Service

class DartStationRepository(
    serviceLocationStore: StoreRoom<List<DartStation>, Service>,
    favouriteRepository: FavouriteRepository
) : ServiceLocationRepository<DartStation>(
    service = Service.IRISH_RAIL,
    serviceLocationStore = serviceLocationStore,
    favouriteRepository = favouriteRepository
)

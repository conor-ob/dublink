package ie.dublinmapper.repository.irishrail.stations

import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import ie.dublinmapper.domain.model.IrishRailStation
import ie.dublinmapper.domain.repository.FavouriteRepository
import ie.dublinmapper.repository.ServiceLocationRepository
import ie.dublinmapper.util.Service

class IrishRailStationRepository(
    serviceLocationStore: StoreRoom<List<IrishRailStation>, Service>
) : ServiceLocationRepository<IrishRailStation>(
    service = Service.IRISH_RAIL,
    serviceLocationStore = serviceLocationStore
)

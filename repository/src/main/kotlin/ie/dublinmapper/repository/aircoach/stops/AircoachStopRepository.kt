package ie.dublinmapper.repository.aircoach.stops

import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import ie.dublinmapper.domain.model.AircoachStop
import ie.dublinmapper.domain.repository.FavouriteRepository
import ie.dublinmapper.repository.ServiceLocationRepository
import ie.dublinmapper.util.Service

class AircoachStopRepository(
    serviceLocationStore: StoreRoom<List<AircoachStop>, Service>,
    favouriteRepository: FavouriteRepository
) : ServiceLocationRepository<AircoachStop>(
    service = Service.AIRCOACH,
    serviceLocationStore = serviceLocationStore,
    favouriteRepository = favouriteRepository
)

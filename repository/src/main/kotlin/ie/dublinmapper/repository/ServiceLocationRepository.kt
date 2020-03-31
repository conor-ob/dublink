package ie.dublinmapper.repository

import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import ie.dublinmapper.domain.model.isFavourite
import ie.dublinmapper.domain.repository.LocationRepository
import ie.dublinmapper.domain.repository.ServiceLocationKey
import io.reactivex.Observable
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation

class ServiceLocationRepository<T : ServiceLocation>(
    private val service: Service,
    private val serviceLocationStore: StoreRoom<List<T>, Service>
) : LocationRepository {

    override fun get(): Observable<List<ServiceLocation>> {
        return serviceLocationStore.get(service).map { it }
    }

    override fun getFavourites(): Observable<List<ServiceLocation>> {
        return get().map { it.filter { serviceLocation -> serviceLocation.isFavourite() } }
    }

    override fun get(key: ServiceLocationKey): Observable<ServiceLocation> {
        return get().map { response -> response.find { it.id == key.locationId } }
    }

    override fun clearCache(service: Service) {
        serviceLocationStore.clear()
    }
}

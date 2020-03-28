package ie.dublinmapper.repository

import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import ie.dublinmapper.domain.model.isFavourite
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.domain.service.EnabledServiceManager
import io.reactivex.Observable
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation

abstract class ServiceLocationRepository<T : ServiceLocation>(
    private val service: Service,
    private val serviceLocationStore: StoreRoom<List<T>, Service>,
    private val enabledServiceManager: EnabledServiceManager
) : Repository<T> {

    private var cache = emptyMap<String, T>()

    private fun fillCache(serviceLocations: List<T>) {
        cache = serviceLocations.associateBy { it.id }
    }

    override fun clearCache() {
        cache = emptyMap()
        serviceLocationStore.clear()
    }

    override fun getAll(): Observable<List<T>> {
        if (enabledServiceManager.isServiceEnabled(service)) {
            return serviceLocationStore.get(service)
                .doOnNext { serviceLocations -> fillCache(serviceLocations) }

        }
        return Observable.just(emptyList())
    }

    override fun getAllFavorites(): Observable<List<T>> {
        return getAll().map { it.filter { serviceLocation -> serviceLocation.isFavourite() } }
    }

//    override fun getAll(): Observable<List<T>> {
//        return Observable.zip(
//            serviceLocationStore.get(service),
//            favouriteRepository.getFavourites(service),
//            BiFunction { serviceLocations, favourites ->
//                val serviceLocationsById = serviceLocations.associateBy { it.id }.toMutableMap()
//                for (favourite in favourites) {
//                    val serviceLocation = serviceLocationsById[favourite.id]
//                    val serviceLocationWithFavourite = serviceLocation!!.cloneWithFavourite(favourite)
//                    serviceLocationsById[serviceLocation.id] = serviceLocationWithFavourite as T
//                }
//                cache = serviceLocationsById
//                return@BiFunction cache.values.toList()
//            }
//        )
//    }

    override fun getById(id: String): Observable<T> {
        val serviceLocation = cache[id]
        if (serviceLocation != null) {
            return Observable.just(serviceLocation)
        }
        return getAll().map { serviceLocations -> serviceLocations.find { it.id == id } }
    }

    override fun refresh(): Observable<Boolean> {
        return serviceLocationStore.fetch(service)
            .doOnNext { serviceLocations -> fillCache(serviceLocations) }
            .map { true }
    }

    override fun getAllById(id: String): Observable<List<T>> {
        throw UnsupportedOperationException()
    }

}

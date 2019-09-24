package ie.dublinmapper.repository

import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import ie.dublinmapper.domain.model.ServiceLocation
import ie.dublinmapper.domain.repository.FavouriteRepository
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.util.Service
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

abstract class ServiceLocationRepository<T : ServiceLocation>(
    private val service: Service,
    private val serviceLocationStore: StoreRoom<List<T>, Service>
) : Repository<T> {

    private var cache = emptyMap<String, T>()

    private fun fillCache(serviceLocations: List<T>) {
        cache = serviceLocations.associateBy { it.id }
    }

    override fun getAll(): Observable<List<T>> {
        return serviceLocationStore.get(service)
            .doOnNext { serviceLocations -> fillCache(serviceLocations) }
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

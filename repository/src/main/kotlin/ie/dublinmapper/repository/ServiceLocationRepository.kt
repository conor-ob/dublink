package ie.dublinmapper.repository

import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import ie.dublinmapper.domain.model.ServiceLocation
import ie.dublinmapper.domain.repository.Repository
import io.reactivex.Observable

abstract class ServiceLocationRepository<T : ServiceLocation>(
    private val key: String,
    private val store: StoreRoom<List<T>, String>
) : Repository<T> {

    private var cache = emptyMap<String, T>()

    private fun fillCache(serviceLocations: List<T>) {
        cache = serviceLocations.associateBy { it.id }
    }

    override fun getAll(): Observable<List<T>> {
        return store.get(key)
            .doOnNext { serviceLocations -> fillCache(serviceLocations) }
    }

    override fun getById(id: String): Observable<T> {
        val serviceLocation = cache[id]
        if (serviceLocation != null) {
            return Observable.just(serviceLocation)
        }
        return getAll().map { serviceLocations -> serviceLocations.find { it.id == id } }
    }

    override fun refresh(): Observable<Boolean> {
        return store.fetch(key)
            .doOnNext { serviceLocations -> fillCache(serviceLocations) }
            .map { true }
    }

    override fun getAllById(id: String): Observable<List<T>> {
        throw UnsupportedOperationException()
    }

}

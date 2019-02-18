package ie.dublinmapper.repository

import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import ie.dublinmapper.domain.model.ServiceLocation
import ie.dublinmapper.domain.repository.Repository
import io.reactivex.Observable

abstract class ServiceLocationRoomRepository<T : ServiceLocation>(
    private val store: StoreRoom<List<T>, String>
) : Repository<T> {

    abstract fun key(): String

    override fun getAll(): Observable<List<T>> {
        return store.get(key())
    }

    override fun getById(id: String): Observable<T> {
        return getAll().map { serviceLocations -> serviceLocations.find { it.id == id } }
    }

    override fun refresh(): Observable<Boolean> {
        return store.fetch(key()).map { true }
    }

    override fun getAllById(id: String): Observable<List<T>> {
        throw UnsupportedOperationException()
    }

}

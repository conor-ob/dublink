package ie.dublinmapper.repository

import com.nytimes.android.external.store3.base.impl.Store
import ie.dublinmapper.domain.model.ServiceLocation
import ie.dublinmapper.domain.repository.Repository
import io.reactivex.Observable

abstract class ServiceLocationRepository<T : ServiceLocation>(
    private val store: Store<List<T>, String>
) : Repository<T> {

    abstract fun key(): String

    override fun getAll(): Observable<List<T>> {
        return store.get(key()).toObservable()
    }

    override fun getById(id: String): Observable<T> {
        return getAll().map { stations -> stations.find { it.id == id } }
    }

    override fun refresh(): Observable<Boolean> {
        return store.fetch(key()).toObservable().map { true }
    }

    override fun getAllById(id: String): Observable<List<T>> {
        throw UnsupportedOperationException()
    }

}

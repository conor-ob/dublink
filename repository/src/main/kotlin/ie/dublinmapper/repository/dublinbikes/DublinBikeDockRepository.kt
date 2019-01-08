package ie.dublinmapper.repository.dublinbikes

import com.nytimes.android.external.store3.base.impl.Store
import ie.dublinmapper.domain.model.dublinbikes.DublinBikesDock
import ie.dublinmapper.domain.repository.Repository
import io.reactivex.Observable
import java.lang.UnsupportedOperationException

class DublinBikeDockRepository(
    private val store: Store<List<DublinBikesDock>, String>
) : Repository<DublinBikesDock> {

    private val key = "dublin_bikes_docks"

    override fun getAll(): Observable<List<DublinBikesDock>> {
        return store.get(key).toObservable()
    }

    override fun getById(id: String): Observable<DublinBikesDock> {
        return getAll().map { docks -> findMatching(id, docks) }
    }

    private fun findMatching(id: String, docks: List<DublinBikesDock>): DublinBikesDock {
        for (dock in docks) {
            if (id == dock.id) {
                return dock
            }
        }
        throw IllegalStateException()
    }

    override fun refresh(): Observable<Boolean> {
        return store.fetch(key).toObservable().map { true }
    }

    override fun getAllById(id: String): Observable<List<DublinBikesDock>> {
        throw UnsupportedOperationException()
    }

}

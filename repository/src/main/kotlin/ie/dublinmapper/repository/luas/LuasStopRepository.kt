package ie.dublinmapper.repository.luas

import com.nytimes.android.external.store3.base.impl.Store
import ie.dublinmapper.domain.model.LuasStop
import ie.dublinmapper.domain.repository.Repository
import io.reactivex.Observable

class LuasStopRepository(
    private val store: Store<List<LuasStop>, String>
) : Repository<LuasStop> {

    private val key = "luas_stops"

    override fun getAll(): Observable<List<LuasStop>> {
        return store.get(key).toObservable()
    }

    override fun getById(id: String): Observable<LuasStop> {
        return getAll().map { stops -> findMatching(id, stops) }
    }

    private fun findMatching(id: String, stops: List<LuasStop>): LuasStop {
        for (stop in stops) {
            if (id == stop.id) {
                return stop
            }
        }
        throw IllegalStateException()
    }

    override fun refresh(): Observable<Boolean> {
        return store.get(key).toObservable().map { true }
    }

    override fun getAllById(id: String): Observable<List<LuasStop>> {
        throw UnsupportedOperationException()
    }

}

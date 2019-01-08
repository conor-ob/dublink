package ie.dublinmapper.repository.dublinbus

import com.nytimes.android.external.store3.base.impl.Store
import ie.dublinmapper.domain.model.dublinbus.DublinBusStop
import ie.dublinmapper.domain.repository.Repository
import io.reactivex.Observable

class DublinBusStopRepository(
    private val store: Store<List<DublinBusStop>, String>
) : Repository<DublinBusStop> {

    private val key = "dublin_bus_stops"

    override fun getAll(): Observable<List<DublinBusStop>> {
        return store.get(key).toObservable()
    }

    override fun getById(id: String): Observable<DublinBusStop> {
        return getAll().map { stops -> findMatching(id, stops) }
    }

    private fun findMatching(id: String, stops: List<DublinBusStop>): DublinBusStop {
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

    override fun getAllById(id: String): Observable<List<DublinBusStop>> {
        throw UnsupportedOperationException()
    }

}

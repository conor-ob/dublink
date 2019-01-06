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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAllById(id: String): Observable<List<DublinBusStop>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun refresh(): Observable<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

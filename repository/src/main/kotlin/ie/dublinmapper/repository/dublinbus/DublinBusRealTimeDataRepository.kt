package ie.dublinmapper.repository.dublinbus

import com.nytimes.android.external.store3.base.impl.Store
import ie.dublinmapper.domain.model.dublinbus.DublinBusRealTimeData
import ie.dublinmapper.domain.repository.Repository
import io.reactivex.Observable
import java.lang.UnsupportedOperationException

class DublinBusRealTimeDataRepository(
    private val store: Store<List<DublinBusRealTimeData>, String>
) : Repository<DublinBusRealTimeData> {

    override fun getAllById(id: String): Observable<List<DublinBusRealTimeData>> {
        return store.get(id).toObservable()
    }

    override fun getById(id: String): Observable<DublinBusRealTimeData> {
        throw UnsupportedOperationException()
    }

    override fun getAll(): Observable<List<DublinBusRealTimeData>> {
        throw UnsupportedOperationException()
    }

    override fun refresh(): Observable<Boolean> {
        throw UnsupportedOperationException()
    }

}

package ie.dublinmapper.repository.dublinbikes

import com.nytimes.android.external.store3.base.impl.Store
import ie.dublinmapper.domain.model.dublinbikes.DublinBikesRealTimeData
import ie.dublinmapper.domain.repository.Repository
import io.reactivex.Observable
import java.lang.UnsupportedOperationException

class DublinBikesRealTimeDataRepository(
    private val store: Store<DublinBikesRealTimeData, String>
) : Repository<DublinBikesRealTimeData> {

    override fun getById(id: String): Observable<DublinBikesRealTimeData> {
        return store.get(id).toObservable()
    }

    override fun getAllById(id: String): Observable<List<DublinBikesRealTimeData>> {
        throw UnsupportedOperationException()
    }

    override fun getAll(): Observable<List<DublinBikesRealTimeData>> {
        throw UnsupportedOperationException()
    }

    override fun refresh(): Observable<Boolean> {
        throw UnsupportedOperationException()
    }

}

package ie.dublinmapper.repository.dublinbus

import com.nytimes.android.external.store3.base.impl.Store
import ie.dublinmapper.domain.model.DublinBusLiveData
import ie.dublinmapper.domain.repository.Repository
import io.reactivex.Observable

class DublinBusLiveDataRepository(
    private val store: Store<List<DublinBusLiveData>, String>
) : Repository<DublinBusLiveData> {

    override fun getAllById(id: String): Observable<List<DublinBusLiveData>> {
        return store.get(id).toObservable()
    }

    override fun getById(id: String): Observable<DublinBusLiveData> {
        throw UnsupportedOperationException()
    }

    override fun getAll(): Observable<List<DublinBusLiveData>> {
        throw UnsupportedOperationException()
    }

    override fun refresh(): Observable<Boolean> {
        throw UnsupportedOperationException()
    }

}

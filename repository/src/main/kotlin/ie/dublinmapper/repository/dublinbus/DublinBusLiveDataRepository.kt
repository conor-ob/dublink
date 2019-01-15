package ie.dublinmapper.repository.dublinbus

import com.nytimes.android.external.store3.base.impl.Store
import ie.dublinmapper.domain.model.LiveData
import ie.dublinmapper.domain.repository.Repository
import io.reactivex.Observable

class DublinBusLiveDataRepository(
    private val store: Store<List<LiveData.DublinBus>, String>
) : Repository<LiveData.DublinBus> {

    override fun getAllById(id: String): Observable<List<LiveData.DublinBus>> {
        return store.get(id).toObservable()
    }

    override fun getById(id: String): Observable<LiveData.DublinBus> {
        throw UnsupportedOperationException()
    }

    override fun getAll(): Observable<List<LiveData.DublinBus>> {
        throw UnsupportedOperationException()
    }

    override fun refresh(): Observable<Boolean> {
        throw UnsupportedOperationException()
    }

}

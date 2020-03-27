package ie.dublinmapper.repository.dublinbus.livedata

import com.nytimes.android.external.store3.base.impl.Store
import ie.dublinmapper.domain.repository.Repository
import io.reactivex.Observable
import io.rtpi.api.DublinBusLiveData

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

    override fun getAllFavorites(): Observable<List<DublinBusLiveData>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun refresh(): Observable<Boolean> {
        throw UnsupportedOperationException()
    }

    override fun clearCache() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

package ie.dublinmapper.repository.dart

import com.nytimes.android.external.store3.base.impl.Store
import ie.dublinmapper.domain.model.DartLiveData
import ie.dublinmapper.domain.repository.Repository
import io.reactivex.Observable

class DartRealTimeDataRepository(
    private val store: Store<List<DartLiveData>, String>
) : Repository<DartLiveData> {

    override fun getAllById(id: String): Observable<List<DartLiveData>> {
        return store.get(id).toObservable()
    }

    override fun getById(id: String): Observable<DartLiveData> {
        throw UnsupportedOperationException()
    }

    override fun getAll(): Observable<List<DartLiveData>> {
        throw UnsupportedOperationException()
    }

    override fun refresh(): Observable<Boolean> {
        throw UnsupportedOperationException()
    }

}

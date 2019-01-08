package ie.dublinmapper.repository.dart

import com.nytimes.android.external.store3.base.impl.Store
import ie.dublinmapper.domain.model.dart.DartRealTimeData
import ie.dublinmapper.domain.repository.Repository
import io.reactivex.Observable
import java.lang.UnsupportedOperationException

class DartRealTimeDataRepository(
    private val store: Store<List<DartRealTimeData>, String>
) : Repository<DartRealTimeData> {

    override fun getAllById(id: String): Observable<List<DartRealTimeData>> {
        return store.get(id).toObservable()
    }

    override fun getById(id: String): Observable<DartRealTimeData> {
        throw UnsupportedOperationException()
    }

    override fun getAll(): Observable<List<DartRealTimeData>> {
        throw UnsupportedOperationException()
    }

    override fun refresh(): Observable<Boolean> {
        throw UnsupportedOperationException()
    }

}

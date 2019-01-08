package ie.dublinmapper.repository.dart

import com.nytimes.android.external.store3.base.impl.Store
import ie.dublinmapper.domain.model.LiveData
import ie.dublinmapper.domain.repository.Repository
import io.reactivex.Observable

class DartLiveDataRepository(
    private val store: Store<List<LiveData.Dart>, String>
) : Repository<LiveData.Dart> {

    override fun getAllById(id: String): Observable<List<LiveData.Dart>> {
        return store.get(id).toObservable()
    }

    override fun getById(id: String): Observable<LiveData.Dart> {
        throw UnsupportedOperationException()
    }

    override fun getAll(): Observable<List<LiveData.Dart>> {
        throw UnsupportedOperationException()
    }

    override fun refresh(): Observable<Boolean> {
        throw UnsupportedOperationException()
    }

}

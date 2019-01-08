package ie.dublinmapper.repository.luas

import com.nytimes.android.external.store3.base.impl.Store
import ie.dublinmapper.domain.model.LiveData
import ie.dublinmapper.domain.repository.Repository
import io.reactivex.Observable

class LuasLiveDataRepository(
    private val store: Store<List<LiveData.Luas>, String>
) : Repository<LiveData.Luas> {

    override fun getAllById(id: String): Observable<List<LiveData.Luas>> {
        return store.get(id).toObservable()
    }

    override fun getById(id: String): Observable<LiveData.Luas> {
        throw UnsupportedOperationException()
    }

    override fun getAll(): Observable<List<LiveData.Luas>> {
        throw UnsupportedOperationException()
    }

    override fun refresh(): Observable<Boolean> {
        throw UnsupportedOperationException()
    }

}

package ie.dublinmapper.repository.luas

import com.nytimes.android.external.store3.base.impl.Store
import ie.dublinmapper.domain.model.luas.LuasRealTImeData
import ie.dublinmapper.domain.repository.Repository
import io.reactivex.Observable
import java.lang.UnsupportedOperationException

class LuasRealTimeDataRepository(
    private val store: Store<List<LuasRealTImeData>, String>
) : Repository<LuasRealTImeData> {

    override fun getAllById(id: String): Observable<List<LuasRealTImeData>> {
        return store.get(id).toObservable()
    }

    override fun getById(id: String): Observable<LuasRealTImeData> {
        throw UnsupportedOperationException()
    }

    override fun getAll(): Observable<List<LuasRealTImeData>> {
        throw UnsupportedOperationException()
    }

    override fun refresh(): Observable<Boolean> {
        throw UnsupportedOperationException()
    }

}

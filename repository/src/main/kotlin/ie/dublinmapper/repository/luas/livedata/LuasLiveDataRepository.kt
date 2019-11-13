package ie.dublinmapper.repository.luas.livedata

import com.nytimes.android.external.store3.base.impl.Store
import ie.dublinmapper.domain.repository.Repository
import io.reactivex.Observable
import io.rtpi.api.LuasLiveData

class LuasLiveDataRepository(
    private val store: Store<List<LuasLiveData>, String>
) : Repository<LuasLiveData> {

    override fun getAllById(id: String): Observable<List<LuasLiveData>> {
        return store.get(id).toObservable()
    }

    override fun getById(id: String): Observable<LuasLiveData> {
        throw UnsupportedOperationException()
    }

    override fun getAll(): Observable<List<LuasLiveData>> {
        throw UnsupportedOperationException()
    }

    override fun getAllFavorites(): Observable<List<LuasLiveData>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun refresh(): Observable<Boolean> {
        throw UnsupportedOperationException()
    }

    override fun clearCache() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

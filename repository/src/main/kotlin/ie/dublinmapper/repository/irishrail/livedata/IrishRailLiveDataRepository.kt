package ie.dublinmapper.repository.irishrail.livedata

import com.nytimes.android.external.store3.base.impl.Store
import ie.dublinmapper.domain.repository.Repository
import io.reactivex.Observable
import io.rtpi.api.IrishRailLiveData

class IrishRailLiveDataRepository(
    private val store: Store<List<IrishRailLiveData>, String>
) : Repository<IrishRailLiveData> {

    override fun getAllById(id: String): Observable<List<IrishRailLiveData>> {
        return store.get(id).toObservable()
    }

    override fun getById(id: String): Observable<IrishRailLiveData> {
        throw UnsupportedOperationException()
    }

    override fun getAll(): Observable<List<IrishRailLiveData>> {
        throw UnsupportedOperationException()
    }

    override fun getAllFavorites(): Observable<List<IrishRailLiveData>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun refresh(): Observable<Boolean> {
        throw UnsupportedOperationException()
    }

    override fun clearCache() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

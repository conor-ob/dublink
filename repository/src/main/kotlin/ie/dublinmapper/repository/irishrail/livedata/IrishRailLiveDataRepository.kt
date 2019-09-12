package ie.dublinmapper.repository.irishrail.livedata

import com.nytimes.android.external.store3.base.impl.Store
import ie.dublinmapper.domain.model.IrishRailLiveData
import ie.dublinmapper.domain.repository.Repository
import io.reactivex.Observable

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

    override fun refresh(): Observable<Boolean> {
        throw UnsupportedOperationException()
    }

}

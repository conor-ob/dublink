package ie.dublinmapper.repository.dublinbikes

import com.nytimes.android.external.store3.base.impl.Store
import ie.dublinmapper.domain.model.DublinBikesLiveData
import ie.dublinmapper.domain.repository.Repository
import io.reactivex.Observable

class DublinBikesRealTimeDataRepository(
    private val store: Store<DublinBikesLiveData, String>
) : Repository<DublinBikesLiveData> {

    override fun getById(id: String): Observable<DublinBikesLiveData> {
        return store.get(id).toObservable()
    }

    override fun getAllById(id: String): Observable<List<DublinBikesLiveData>> {
        throw UnsupportedOperationException()
    }

    override fun getAll(): Observable<List<DublinBikesLiveData>> {
        throw UnsupportedOperationException()
    }

    override fun refresh(): Observable<Boolean> {
        throw UnsupportedOperationException()
    }

}

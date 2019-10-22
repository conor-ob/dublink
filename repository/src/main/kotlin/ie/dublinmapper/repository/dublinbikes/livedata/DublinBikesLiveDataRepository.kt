package ie.dublinmapper.repository.dublinbikes.livedata

import com.nytimes.android.external.store3.base.impl.Store
import ie.dublinmapper.domain.repository.Repository
import io.reactivex.Observable
import io.rtpi.api.DublinBikesLiveData

class DublinBikesLiveDataRepository(
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

package ie.dublinmapper.repository.dublinbikes

import com.nytimes.android.external.store3.base.impl.Store
import ie.dublinmapper.domain.model.LiveData
import ie.dublinmapper.domain.repository.Repository
import io.reactivex.Observable

class DublinBikesLiveDataRepository(
    private val store: Store<LiveData.DublinBikes, String>
) : Repository<LiveData.DublinBikes> {

    override fun getById(id: String): Observable<LiveData.DublinBikes> {
        return store.get(id).toObservable()
    }

    override fun getAllById(id: String): Observable<List<LiveData.DublinBikes>> {
        throw UnsupportedOperationException()
    }

    override fun getAll(): Observable<List<LiveData.DublinBikes>> {
        throw UnsupportedOperationException()
    }

    override fun refresh(): Observable<Boolean> {
        throw UnsupportedOperationException()
    }

}

package ie.dublinmapper.repository.aircoach.livedata

import com.nytimes.android.external.store3.base.impl.Store
import ie.dublinmapper.domain.model.AircoachLiveData
import ie.dublinmapper.domain.repository.Repository
import io.reactivex.Observable

class AircoachLiveDataRepository(
    private val store: Store<List<AircoachLiveData>, String>
) : Repository<AircoachLiveData> {

    override fun getAllById(id: String): Observable<List<AircoachLiveData>> {
        return store.get(id).toObservable()
    }

    override fun getById(id: String): Observable<AircoachLiveData> {
        throw UnsupportedOperationException()
    }

    override fun getAll(): Observable<List<AircoachLiveData>> {
        throw UnsupportedOperationException()
    }

    override fun refresh(): Observable<Boolean> {
        throw UnsupportedOperationException()
    }

}

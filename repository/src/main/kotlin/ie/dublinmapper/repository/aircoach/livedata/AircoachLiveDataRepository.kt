package ie.dublinmapper.repository.aircoach.livedata

import com.nytimes.android.external.store3.base.impl.Store
import ie.dublinmapper.domain.repository.Repository
import io.reactivex.Observable
import io.rtpi.api.AircoachLiveData

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

    override fun getAllFavorites(): Observable<List<AircoachLiveData>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun refresh(): Observable<Boolean> {
        throw UnsupportedOperationException()
    }

    override fun clearCache() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

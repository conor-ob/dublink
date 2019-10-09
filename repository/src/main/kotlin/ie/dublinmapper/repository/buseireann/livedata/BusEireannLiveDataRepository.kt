package ie.dublinmapper.repository.buseireann.livedata

import com.nytimes.android.external.store3.base.impl.Store
import ie.dublinmapper.domain.repository.Repository
import io.reactivex.Observable
import io.rtpi.api.BusEireannLiveData
import org.threeten.bp.LocalTime

class BusEireannLiveDataRepository(
    private val store: Store<List<BusEireannLiveData>, String>
) : Repository<BusEireannLiveData> {

    override fun getAllById(id: String): Observable<List<BusEireannLiveData>> {
        return store.get(id).toObservable()
    }

    override fun getById(id: String): Observable<BusEireannLiveData> {
        throw UnsupportedOperationException()
    }

    override fun getAll(): Observable<List<BusEireannLiveData>> {
        throw UnsupportedOperationException()
    }

    override fun refresh(): Observable<Boolean> {
        throw UnsupportedOperationException()
    }

}

package ie.dublinmapper.repository.dart

import com.nytimes.android.external.store3.base.impl.Store
import ie.dublinmapper.domain.model.DartStation
import ie.dublinmapper.domain.repository.Repository
import io.reactivex.Observable

class DartStationRepository(
    private val store: Store<List<DartStation>, String>
) : Repository<DartStation> {

    private val key = "dart_stations"

    override fun getAll(): Observable<List<DartStation>> {
        return store.get(key).toObservable()
    }

    override fun getById(id: String): Observable<DartStation> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAllById(id: String): Observable<List<DartStation>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun refresh(): Observable<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

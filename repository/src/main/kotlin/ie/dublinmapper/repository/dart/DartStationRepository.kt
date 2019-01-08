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
        return getAll().map { stations -> findMatching(id, stations) }
    }

    private fun findMatching(id: String, stations: List<DartStation>): DartStation {
        for (station in stations) {
            if (id == station.id) {
                return station
            }
        }
        throw IllegalStateException()
    }

    override fun refresh(): Observable<Boolean> {
        return store.fetch(key).toObservable().map { true }
    }

    override fun getAllById(id: String): Observable<List<DartStation>> {
        throw UnsupportedOperationException()
    }

}

package ie.dublinmapper.repository.luas

import com.nytimes.android.external.store3.base.impl.Store
import ie.dublinmapper.domain.model.luas.LuasStop
import ie.dublinmapper.domain.repository.Repository
import io.reactivex.Observable

class LuasStopRepository(
    private val store: Store<List<LuasStop>, String>
) : Repository<LuasStop> {

    private val key = "luas_stops"

    override fun getAll(): Observable<List<LuasStop>> {
        return store.get(key).toObservable()
    }

    override fun getById(id: String): Observable<LuasStop> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAllById(id: String): Observable<List<LuasStop>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun refresh(): Observable<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

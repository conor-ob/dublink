package ie.dublinmapper.repository.luas

import ie.dublinmapper.domain.model.luas.LuasStop
import ie.dublinmapper.domain.repository.Repository
import io.reactivex.Observable

class LuasStopRepository : Repository<LuasStop> {

    override fun getAll(): Observable<List<LuasStop>> {
        return Observable.just(emptyList())
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

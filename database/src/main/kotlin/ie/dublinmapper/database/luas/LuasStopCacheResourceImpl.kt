package ie.dublinmapper.database.luas

import ie.dublinmapper.datamodel.TxRunner
import ie.dublinmapper.datamodel.luas.*
import io.reactivex.Maybe

class LuasStopCacheResourceImpl(
    private val luasStopLocationDao: LuasStopLocationDao,
    private val luasStopServiceDao: LuasStopServiceDao,
    private val luasStopDao: LuasStopDao,
    private val txRunner: TxRunner
) : LuasStopCacheResource {

    override fun selectStops(): Maybe<List<LuasStopEntity>> {
        return luasStopDao.selectAll()
    }

    override fun insertStops(stops: List<LuasStopEntity>) {
        txRunner.runInTx {
            luasStopLocationDao.deleteAll() //TODO test cascade delete
            luasStopLocationDao.insertAll(stops.map { it.location })
            luasStopServiceDao.insertAll(stops.flatMap { it.services })
        }
    }

}

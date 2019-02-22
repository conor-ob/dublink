package ie.dublinmapper.luas

import ie.dublinmapper.data.TxRunner
import ie.dublinmapper.data.luas.*
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

    override fun insertStops(stops: Pair<List<LuasStopLocationEntity>, List<LuasStopServiceEntity>>) {
        txRunner.runInTx {
            luasStopLocationDao.insertAll(stops.first)
            luasStopServiceDao.insertAll(stops.second)
        }
    }

}

package ie.dublinmapper.database.swordsexpress

import ie.dublinmapper.datamodel.TxRunner
import ie.dublinmapper.datamodel.swordsexpress.*
import io.reactivex.Maybe

class SwordsExpressStopCacheResourceImpl(
    private val swordsExpressStopLocationDao: SwordsExpressStopLocationDao,
    private val swordsExpressStopServiceDao: SwordsExpressStopServiceDao,
    private val swordsExpressStopDao: SwordsExpressStopDao,
    private val txRunner: TxRunner
) : SwordsExpressStopCacheResource {

    override fun selectStops(): Maybe<List<SwordsExpressStopEntity>> {
        return swordsExpressStopDao.selectAll()
    }

    override fun insertStops(stops: Pair<List<SwordsExpressStopLocationEntity>, List<SwordsExpressStopServiceEntity>>) {
        txRunner.runInTx {
            swordsExpressStopLocationDao.insertAll(stops.first)
            swordsExpressStopServiceDao.insertAll(stops.second)
        }
    }

}

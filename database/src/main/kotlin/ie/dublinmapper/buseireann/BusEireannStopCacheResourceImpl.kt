package ie.dublinmapper.buseireann

import ie.dublinmapper.data.TxRunner
import ie.dublinmapper.data.buseireann.*
import io.reactivex.Maybe

class BusEireannStopCacheResourceImpl(
    private val busEireannStopLocationDao: BusEireannStopLocationDao,
    private val busEireannStopServiceDao: BusEireannStopServiceDao,
    private val busEireannStopDao: BusEireannStopDao,
    private val txRunner: TxRunner
) : BusEireannStopCacheResource {

    override fun selectStops(): Maybe<List<BusEireannStopEntity>> {
        return busEireannStopDao.selectAll()
    }

    override fun insertStops(stops: Pair<List<BusEireannStopLocationEntity>, List<BusEireannStopServiceEntity>>) {
        txRunner.runInTx {
            busEireannStopLocationDao.insertAll(stops.first)
            busEireannStopServiceDao.insertAll(stops.second)
        }
    }

}

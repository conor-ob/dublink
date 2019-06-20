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

    override fun insertStops(stops: List<BusEireannStopEntity>) {
        txRunner.runInTx {
            busEireannStopLocationDao.deleteAll() //TODO test cascade delete
            busEireannStopLocationDao.insertAll(stops.map { it.location })
            busEireannStopServiceDao.insertAll(stops.flatMap { it.services })
        }
    }

}

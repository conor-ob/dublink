package ie.dublinmapper.dublinbus

import ie.dublinmapper.data.TxRunner
import ie.dublinmapper.data.dublinbus.*
import io.reactivex.Maybe

class DublinBusStopCacheResourceImpl(
    private val dublinBusStopLocationDao: DublinBusStopLocationDao,
    private val dublinBusStopServiceDao: DublinBusStopServiceDao,
    private val dublinBusStopDao: DublinBusStopDao,
    private val txRunner: TxRunner
) : DublinBusStopCacheResource {

    override fun selectStops(): Maybe<List<DublinBusStopEntity>> {
        return dublinBusStopDao.selectAll()
    }

    override fun insertStops(stops: List<DublinBusStopEntity>) {
        txRunner.runInTx {
            dublinBusStopLocationDao.deleteAll() //TODO test cascade delete
            dublinBusStopLocationDao.insertAll(stops.map { it.location })
            dublinBusStopServiceDao.insertAll(stops.flatMap { it.services })
        }
    }

}

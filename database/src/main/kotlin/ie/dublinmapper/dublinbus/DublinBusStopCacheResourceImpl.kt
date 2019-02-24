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

    override fun insertStops(stops: Pair<List<DublinBusStopLocationEntity>, List<DublinBusStopServiceEntity>>) {
        txRunner.runInTx {
            dublinBusStopLocationDao.insertAll(stops.first)
            dublinBusStopServiceDao.insertAll(stops.second)
        }
    }

}

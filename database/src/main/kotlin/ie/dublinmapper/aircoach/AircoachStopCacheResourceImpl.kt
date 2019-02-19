package ie.dublinmapper.aircoach

import ie.dublinmapper.data.TxRunner
import ie.dublinmapper.data.aircoach.*
import io.reactivex.Maybe

class AircoachStopCacheResourceImpl(
    private val aircoachStopLocationDao: AircoachStopLocationDao,
    private val aircoachStopServiceDao: AircoachStopServiceDao,
    private val aircoachStopDao: AircoachStopDao,
    private val txRunner: TxRunner
) : AircoachStopCacheResource {

    override fun selectStops(): Maybe<List<AircoachStopEntity>> {
        return aircoachStopDao.selectAll()
    }

    override fun insertStops(stops: Pair<List<AircoachStopLocationEntity>, List<AircoachStopServiceEntity>>) {
        txRunner.runInTx {
            aircoachStopLocationDao.insertAll(stops.first)
            aircoachStopServiceDao.insertAll(stops.second)
        }
    }

}

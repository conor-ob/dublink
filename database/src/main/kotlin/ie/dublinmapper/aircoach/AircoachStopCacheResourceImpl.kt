package ie.dublinmapper.aircoach

import ie.dublinmapper.datamodel.TxRunner
import ie.dublinmapper.datamodel.aircoach.*
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

    override fun insertStops(stops: List<AircoachStopEntity>) {
        txRunner.runInTx {
            aircoachStopLocationDao.deleteAll() //TODO test cascade delete
            aircoachStopLocationDao.insertAll(stops.map { it.location })
            aircoachStopServiceDao.insertAll(stops.flatMap { it.services })
        }
    }

}

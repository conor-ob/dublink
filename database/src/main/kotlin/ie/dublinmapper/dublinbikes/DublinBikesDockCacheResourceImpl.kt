package ie.dublinmapper.dublinbikes

import ie.dublinmapper.data.TxRunner
import ie.dublinmapper.data.aircoach.AircoachStopEntity
import ie.dublinmapper.data.dublinbikes.*
import io.reactivex.Maybe

class DublinBikesDockCacheResourceImpl(
    private val dublinBikesDockLocationDao: DublinBikesDockLocationDao,
    private val dublinBikesDockServiceDao: DublinBikesDockServiceDao,
    private val dublinBikesDockDao: DublinBikesDockDao,
    private val txRunner: TxRunner
) : DublinBikesDockCacheResource {

    override fun selectDocks(): Maybe<List<DublinBikesDockEntity>> {
        return dublinBikesDockDao.selectAll()
    }

    override fun insertDocks(docks: List<DublinBikesDockEntity>) {
        txRunner.runInTx {
            dublinBikesDockLocationDao.deleteAll() //TODO test cascade delete
            dublinBikesDockLocationDao.insertAll(docks.map { it.location })
            dublinBikesDockServiceDao.insertAll(docks.flatMap { it.services })
        }
    }

}

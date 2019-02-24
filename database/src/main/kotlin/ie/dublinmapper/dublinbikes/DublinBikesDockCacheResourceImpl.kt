package ie.dublinmapper.dublinbikes

import ie.dublinmapper.data.TxRunner
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

    override fun insertDocks(docks: Pair<List<DublinBikesDockLocationEntity>, List<DublinBikesDockServiceEntity>>) {
        txRunner.runInTx {
            dublinBikesDockLocationDao.insertAll(docks.first)
            dublinBikesDockServiceDao.insertAll(docks.second)
        }
    }

}

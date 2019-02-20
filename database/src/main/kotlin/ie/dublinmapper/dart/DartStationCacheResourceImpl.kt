package ie.dublinmapper.dart

import ie.dublinmapper.data.TxRunner
import ie.dublinmapper.data.dart.*
import io.reactivex.Maybe

class DartStationCacheResourceImpl(
    private val dartStationLocationDao: DartStationLocationDao,
    private val dartStationServiceDao: DartStationServiceDao,
    private val dartStationDao: DartStationDao,
    private val txRunner: TxRunner
) : DartStationCacheResource {

    override fun selectStops(): Maybe<List<DartStationEntity>> {
        return dartStationDao.selectAll()
    }

    override fun insertStops(stops: Pair<List<DartStationLocationEntity>, List<DartStationServiceEntity>>) {
        txRunner.runInTx {
            dartStationLocationDao.insertAll(stops.first)
            dartStationServiceDao.insertAll(stops.second)
        }
    }

}

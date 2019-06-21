package ie.dublinmapper.dart

import ie.dublinmapper.datamodel.TxRunner
import ie.dublinmapper.datamodel.dart.*
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

    override fun insertStops(stations: List<DartStationEntity>) {
        txRunner.runInTx {
            dartStationLocationDao.deleteAll() //TODO test cascade delete
            dartStationLocationDao.insertAll(stations.map { it.location })
            dartStationServiceDao.insertAll(stations.flatMap { it.services })
        }
    }

}

package ie.dublinmapper.database.irishrail

import ie.dublinmapper.datamodel.TxRunner
import ie.dublinmapper.datamodel.favourite.FavouriteDao
import ie.dublinmapper.datamodel.favourite.FavouriteEntity
import ie.dublinmapper.datamodel.irishrail.*
import io.reactivex.Maybe
import io.rtpi.api.Service

class IrishRailStationLocalResourceImpl(
    private val irishRailStationLocationDao: IrishRailStationLocationDao,
    private val irishRailStationServiceDao: IrishRailStationServiceDao,
    private val irishRailStationDao: IrishRailStationDao,
    private val favouriteDao: FavouriteDao,
    private val txRunner: TxRunner
) : IrishRailStationLocalResource {

    override fun selectStations(): Maybe<List<IrishRailStationEntity>> {
        return irishRailStationDao.selectAll()
    }

    override fun selectFavouriteStations(): Maybe<List<FavouriteEntity>> {
        return favouriteDao.selectAll(Service.IRISH_RAIL)
    }

    override fun insertStations(stations: List<IrishRailStationEntity>) {
        txRunner.runInTx {
            irishRailStationLocationDao.deleteAll() //TODO test cascade delete
            irishRailStationLocationDao.insertAll(stations.map { it.location })
            irishRailStationServiceDao.insertAll(stations.flatMap { it.services })
        }
    }

}

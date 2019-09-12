package ie.dublinmapper.database.aircoach

import ie.dublinmapper.datamodel.TxRunner
import ie.dublinmapper.datamodel.aircoach.*
import ie.dublinmapper.datamodel.favourite.FavouriteDao
import ie.dublinmapper.datamodel.favourite.FavouriteEntity
import ie.dublinmapper.util.Service
import io.reactivex.Maybe

class AircoachStopLocalResourceImpl(
    private val aircoachStopLocationDao: AircoachStopLocationDao,
    private val aircoachStopServiceDao: AircoachStopServiceDao,
    private val aircoachStopDao: AircoachStopDao,
    private val favouriteDao: FavouriteDao,
    private val txRunner: TxRunner
) : AircoachStopLocalResource {

    override fun selectStops(): Maybe<List<AircoachStopEntity>> {
        return aircoachStopDao.selectAll()
    }

    override fun selectFavouriteStops(): Maybe<List<FavouriteEntity>> {
        return favouriteDao.selectAll(Service.AIRCOACH)
    }

    override fun insertStops(stops: List<AircoachStopEntity>) {
        txRunner.runInTx {
            aircoachStopLocationDao.deleteAll() //TODO test cascade delete
            aircoachStopLocationDao.insertAll(stops.map { it.location })
            aircoachStopServiceDao.insertAll(stops.flatMap { it.services })
        }
    }

}

package ie.dublinmapper.database.buseireann

import ie.dublinmapper.datamodel.TxRunner
import ie.dublinmapper.datamodel.buseireann.*
import ie.dublinmapper.datamodel.favourite.FavouriteDao
import ie.dublinmapper.datamodel.favourite.FavouriteEntity
import ie.dublinmapper.util.Service
import io.reactivex.Maybe

class BusEireannStopLocalResourceImpl(
    private val busEireannStopLocationDao: BusEireannStopLocationDao,
    private val busEireannStopServiceDao: BusEireannStopServiceDao,
    private val busEireannStopDao: BusEireannStopDao,
    private val favouriteDao: FavouriteDao,
    private val txRunner: TxRunner
) : BusEireannStopLocalResource {

    override fun selectStops(): Maybe<List<BusEireannStopEntity>> {
        return busEireannStopDao.selectAll()
    }

    override fun selectFavouriteStops(): Maybe<List<FavouriteEntity>> {
        return favouriteDao.selectAll(Service.BUS_EIREANN)
    }

    override fun insertStops(stops: List<BusEireannStopEntity>) {
        txRunner.runInTx {
            busEireannStopLocationDao.deleteAll() //TODO test cascade delete
            busEireannStopLocationDao.insertAll(stops.map { it.location })
            busEireannStopServiceDao.insertAll(stops.flatMap { it.services })
        }
    }

}

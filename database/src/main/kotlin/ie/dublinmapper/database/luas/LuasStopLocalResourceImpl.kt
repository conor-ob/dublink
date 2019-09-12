package ie.dublinmapper.database.luas

import ie.dublinmapper.datamodel.TxRunner
import ie.dublinmapper.datamodel.favourite.FavouriteDao
import ie.dublinmapper.datamodel.favourite.FavouriteEntity
import ie.dublinmapper.datamodel.luas.*
import ie.dublinmapper.util.Service
import io.reactivex.Maybe

class LuasStopLocalResourceImpl(
    private val luasStopLocationDao: LuasStopLocationDao,
    private val luasStopServiceDao: LuasStopServiceDao,
    private val luasStopDao: LuasStopDao,
    private val favouriteDao: FavouriteDao,
    private val txRunner: TxRunner
) : LuasStopLocalResource {

    override fun selectStops(): Maybe<List<LuasStopEntity>> {
        return luasStopDao.selectAll()
    }

    override fun selectFavouriteStops(): Maybe<List<FavouriteEntity>> {
        return favouriteDao.selectAll(Service.LUAS)
    }

    override fun insertStops(stops: List<LuasStopEntity>) {
        txRunner.runInTx {
            luasStopLocationDao.deleteAll() //TODO test cascade delete
            luasStopLocationDao.insertAll(stops.map { it.location })
            luasStopServiceDao.insertAll(stops.flatMap { it.services })
        }
    }

}

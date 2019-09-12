package ie.dublinmapper.database.swordsexpress

import ie.dublinmapper.datamodel.TxRunner
import ie.dublinmapper.datamodel.favourite.FavouriteDao
import ie.dublinmapper.datamodel.favourite.FavouriteEntity
import ie.dublinmapper.datamodel.swordsexpress.*
import ie.dublinmapper.util.Service
import io.reactivex.Maybe

class SwordsExpressStopLocalResourceImpl(
    private val swordsExpressStopLocationDao: SwordsExpressStopLocationDao,
    private val swordsExpressStopServiceDao: SwordsExpressStopServiceDao,
    private val swordsExpressStopDao: SwordsExpressStopDao,
    private val favouriteDao: FavouriteDao,
    private val txRunner: TxRunner
) : SwordsExpressStopLocalResource {

    override fun selectStops(): Maybe<List<SwordsExpressStopEntity>> {
        return swordsExpressStopDao.selectAll()
    }

    override fun selectFavouriteStops(): Maybe<List<FavouriteEntity>> {
        return favouriteDao.selectAll(Service.SWORDS_EXPRESS)
    }

    override fun insertStops(stops: Pair<List<SwordsExpressStopLocationEntity>, List<SwordsExpressStopServiceEntity>>) {
        txRunner.runInTx {
            swordsExpressStopLocationDao.insertAll(stops.first)
            swordsExpressStopServiceDao.insertAll(stops.second)
        }
    }

}

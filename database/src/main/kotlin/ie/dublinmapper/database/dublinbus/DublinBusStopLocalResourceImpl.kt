package ie.dublinmapper.database.dublinbus

import ie.dublinmapper.datamodel.TxRunner
import ie.dublinmapper.datamodel.dublinbus.*
import ie.dublinmapper.datamodel.favourite.FavouriteDao
import ie.dublinmapper.datamodel.favourite.FavouriteEntity
import ie.dublinmapper.util.Service
import io.reactivex.Maybe

class DublinBusStopLocalResourceImpl(
    private val dublinBusStopLocationDao: DublinBusStopLocationDao,
    private val dublinBusStopServiceDao: DublinBusStopServiceDao,
    private val dublinBusStopDao: DublinBusStopDao,
    private val favouriteDao: FavouriteDao,
    private val txRunner: TxRunner
) : DublinBusStopLocalResource {

    override fun selectStops(): Maybe<List<DublinBusStopEntity>> {
        return dublinBusStopDao.selectAll()
    }

    override fun selectFavouriteStops(): Maybe<List<FavouriteEntity>> {
        return favouriteDao.selectAll(Service.DUBLIN_BUS)
    }

    override fun insertStops(stops: List<DublinBusStopEntity>) {
        txRunner.runInTx {
            dublinBusStopLocationDao.deleteAll() //TODO test cascade delete
            dublinBusStopLocationDao.insertAll(stops.map { it.location })
            dublinBusStopServiceDao.insertAll(stops.flatMap { it.services })
        }
    }

}

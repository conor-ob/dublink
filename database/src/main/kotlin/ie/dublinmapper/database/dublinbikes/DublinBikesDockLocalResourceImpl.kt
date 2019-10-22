package ie.dublinmapper.database.dublinbikes

import ie.dublinmapper.datamodel.TxRunner
import ie.dublinmapper.datamodel.dublinbikes.*
import ie.dublinmapper.datamodel.favourite.FavouriteDao
import ie.dublinmapper.datamodel.favourite.FavouriteEntity
import io.reactivex.Maybe
import io.rtpi.api.Service

class DublinBikesDockLocalResourceImpl(
    private val dublinBikesDockLocationDao: DublinBikesDockLocationDao,
    private val dublinBikesDockServiceDao: DublinBikesDockServiceDao,
    private val dublinBikesDockDao: DublinBikesDockDao,
    private val favouriteDao: FavouriteDao,
    private val txRunner: TxRunner
) : DublinBikesDockLocalResource {

    override fun selectDocks(): Maybe<List<DublinBikesDockEntity>> {
        return dublinBikesDockDao.selectAll()
    }

    override fun selectFavouriteDocks(): Maybe<List<FavouriteEntity>> {
        return favouriteDao.selectAll(Service.DUBLIN_BIKES)
    }

    override fun insertDocks(docks: List<DublinBikesDockEntity>) {
        txRunner.runInTx {
            dublinBikesDockLocationDao.deleteAll() //TODO test cascade delete
            dublinBikesDockLocationDao.insertAll(docks.map { it.location })
            dublinBikesDockServiceDao.insertAll(docks.flatMap { it.services })
        }
    }

}

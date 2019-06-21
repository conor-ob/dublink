package ie.dublinmapper.favourite

import ie.dublinmapper.data.TxRunner
import ie.dublinmapper.data.favourite.*
import io.reactivex.Maybe

class FavouriteServiceLocationCacheResourceImpl(
    private val favouriteLocationDao: FavouriteLocationDao,
    private val favouriteServiceDao: FavouriteServiceDao,
    private val favouriteDao: FavouriteDao,
    private val txRunner: TxRunner
) : FavouriteServiceLocationCacheResource {

    override fun selectFavourites(): Maybe<List<FavouriteEntity>> {
        return favouriteDao.selectAll()
    }

    override fun insertFavourite(favourite: FavouriteEntity) {
        insertFavourites(listOf(favourite))
    }

    override fun removeFavourite(favourite: FavouriteEntity) {
        favouriteLocationDao.delete(favourite.location)
    }

    override fun insertFavourites(favourites: List<FavouriteEntity>) {
        txRunner.runInTx {
            favouriteLocationDao.insertAll(favourites.map { it.location })
            favouriteServiceDao.insertAll(favourites.flatMap { it.services })
        }
    }

}

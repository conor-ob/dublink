package ie.dublinmapper.favourite

import ie.dublinmapper.datamodel.TxRunner
import ie.dublinmapper.datamodel.favourite.*
import ie.dublinmapper.util.Service
import io.reactivex.Completable
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

    override fun selectFavourite(id: String, service: Service): Maybe<FavouriteEntity> {
        return favouriteDao.select(FavouriteKey(id, service))
    }

    override fun insertFavourite(favourite: FavouriteEntity): Completable {
        val locationInsert = favouriteLocationDao.insert(favourite.location)
        val serviceInsert = favouriteServiceDao.insertAll(favourite.services)
        return Completable.complete()
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

    override fun countFavourites(): Maybe<Long> {
        return favouriteLocationDao.count()
    }

}

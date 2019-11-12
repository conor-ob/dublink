package ie.dublinmapper.favourite

import com.squareup.sqldelight.runtime.rx.asObservable
import com.squareup.sqldelight.runtime.rx.mapToList
import ie.dublinmapper.database.Database
import ie.dublinmapper.datamodel.favourite.*
import ie.dublinmapper.domain.model.Favourite
import io.reactivex.Completable
import io.reactivex.Observable
import io.rtpi.api.Service

class SqlDelightFavouriteServiceLocationLocalResource(
    private val database: Database
) : FavouriteServiceLocationLocalResource {

    override fun selectFavourites(): Observable<List<Favourite>> {
        return Observable.empty()
//        return database.favouriteServiceLocationEntityQueries.selectAll()
//            .asObservable()
//            .mapToList()
//            .map { favourites ->
//                favourites.map {
//                    Favourite(
//                        id = it.id,
//                        name = it.name,
//                        service = it.service,
//                        order = it.order,
//                        routes = emptyMap()
//                    )
//                }
//            }
    }

    override fun selectFavourite(id: String, service: Service): Observable<Favourite> {
//        return favouriteDao.select(FavouriteKey(id, service))
        TODO()
    }

    override fun insertFavourite(favourite: Favourite): Completable {
//        val locationInsert = favouriteLocationDao.insert(favourite.location)
//        val serviceInsert = favouriteServiceDao.insertAll(favourite.services)
//        return Completable.complete()
        TODO()
    }

    override fun removeFavourite(favourite: Favourite) {
//        favouriteLocationDao.delete(favourite.location)
        TODO()
    }

    override fun insertFavourites(favourites: List<Favourite>) {
//        txRunner.runInTx {
//            favouriteLocationDao.insertAll(favourites.map { it.location })
//            favouriteServiceDao.insertAll(favourites.flatMap { it.services })
//        }
        TODO()
    }

    override fun countFavourites(): Observable<Long> {
//        return favouriteLocationDao.count()
        TODO()
    }

}

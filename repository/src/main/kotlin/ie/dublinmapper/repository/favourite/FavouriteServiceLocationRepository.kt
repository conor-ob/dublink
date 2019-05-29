package ie.dublinmapper.repository.favourite

import ie.dublinmapper.data.favourite.FavouriteDao
import ie.dublinmapper.domain.model.Favourite
import ie.dublinmapper.domain.repository.FavouriteRepository
import ie.dublinmapper.util.Operator
import ie.dublinmapper.util.Service
import io.reactivex.Observable

class FavouriteServiceLocationRepository(
    private val dao: FavouriteDao
) : FavouriteRepository {

    override fun saveFavourites(favourites: List<Favourite>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getFavourites(): Observable<List<Favourite>> {
//        return dao.selectAll()
//            .map { favouriteEntities ->
//                favouriteEntities.map {
//                    Favourite(
//                        id = it.location.id,
//                        name = it.location.name,
//                        service = it.location.service,
//                        routes = emptyMap() //TODO
//                    )
//                }
//            }
//            .toObservable()
        return Observable.just(
            listOf(
                Favourite(
                    id = "BROCK",
                    name = "Blackrock Dart",
                    service = Service.IRISH_RAIL,
                    routes = mapOf(
                        Operator.COMMUTER to setOf(Operator.COMMUTER.fullName),
                        Operator.DART to setOf(Operator.DART.fullName),
                        Operator.INTERCITY to setOf(Operator.INTERCITY.fullName)
                    )
                ),
                Favourite(
                    id = "PERSE",
                    name = "Pearse Dart",
                    service = Service.IRISH_RAIL,
                    routes = mapOf(
                        Operator.COMMUTER to setOf(Operator.COMMUTER.fullName),
                        Operator.DART to setOf(Operator.DART.fullName),
                        Operator.INTERCITY to setOf(Operator.INTERCITY.fullName)
                    )
                )
            )
        )
    }

}

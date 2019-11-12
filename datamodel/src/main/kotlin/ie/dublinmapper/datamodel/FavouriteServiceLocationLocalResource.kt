package ie.dublinmapper.datamodel

import ie.dublinmapper.domain.model.Favourite
import io.reactivex.Completable
import io.reactivex.Observable
import io.rtpi.api.Service

interface FavouriteServiceLocationLocalResource {

    fun selectFavourites(): Observable<List<Favourite>>

    fun insertFavourite(favourite: Favourite): Completable

    fun removeFavourite(favourite: Favourite)

    fun insertFavourites(favourites: List<Favourite>)

    fun countFavourites(): Observable<Long>

    fun selectFavourite(id: String, service: Service): Observable<Favourite>

}

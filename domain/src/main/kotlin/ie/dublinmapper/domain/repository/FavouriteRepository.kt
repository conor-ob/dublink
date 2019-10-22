package ie.dublinmapper.domain.repository

import ie.dublinmapper.domain.model.Favourite
import io.reactivex.Completable
import io.reactivex.Observable
import io.rtpi.api.Service

interface FavouriteRepository {

    fun saveFavourite(favourite: Favourite): Completable

    fun removeFavourite(favourite: Favourite)

    fun updateFavourites(favourites: List<Favourite>)

    fun getFavourites(): Observable<List<Favourite>>

    fun getFavourites(service: Service): Observable<List<Favourite>>

    fun getFavourite(id: String, service: Service): Observable<Favourite>

}

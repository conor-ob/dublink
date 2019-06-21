package ie.dublinmapper.domain.repository

import ie.dublinmapper.domain.model.Favourite
import io.reactivex.Observable

interface FavouriteRepository {

    fun saveFavourite(favourite: Favourite) {
        saveFavourites(listOf(favourite))
    }

    fun removeFavourite(favourite: Favourite)

    fun saveFavourites(favourites: List<Favourite>)

    fun getFavourites(): Observable<List<Favourite>>

}

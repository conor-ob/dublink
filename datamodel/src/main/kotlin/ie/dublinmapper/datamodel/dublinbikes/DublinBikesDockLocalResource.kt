package ie.dublinmapper.datamodel.dublinbikes

import ie.dublinmapper.datamodel.favourite.FavouriteEntity
import io.reactivex.Maybe

interface DublinBikesDockLocalResource {

    fun selectDocks(): Maybe<List<DublinBikesDockEntity>>

    fun selectFavouriteDocks(): Maybe<List<FavouriteEntity>>

    fun insertDocks(docks: List<DublinBikesDockEntity>)

}

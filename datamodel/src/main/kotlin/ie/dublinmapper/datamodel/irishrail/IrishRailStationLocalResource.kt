package ie.dublinmapper.datamodel.irishrail

import ie.dublinmapper.datamodel.favourite.FavouriteEntity
import io.reactivex.Maybe

interface IrishRailStationLocalResource {

    fun selectStations(): Maybe<List<IrishRailStationEntity>>

    fun selectFavouriteStations(): Maybe<List<FavouriteEntity>>

    fun insertStations(stations: List<IrishRailStationEntity>)

}

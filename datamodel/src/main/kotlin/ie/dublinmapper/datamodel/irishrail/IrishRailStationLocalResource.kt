package ie.dublinmapper.datamodel.irishrail

import io.reactivex.Observable
import io.rtpi.api.IrishRailStation

interface IrishRailStationLocalResource {

    fun selectStations(): Observable<List<IrishRailStation>>

    fun insertStations(stations: List<IrishRailStation>)

}

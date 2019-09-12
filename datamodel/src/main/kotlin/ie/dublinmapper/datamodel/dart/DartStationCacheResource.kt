package ie.dublinmapper.datamodel.dart

import io.reactivex.Maybe

interface DartStationCacheResource {

    fun selectStops(): Maybe<List<DartStationEntity>>

    fun insertStops(stations: List<DartStationEntity>)

}

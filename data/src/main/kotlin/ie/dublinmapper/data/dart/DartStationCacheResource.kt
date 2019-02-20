package ie.dublinmapper.data.dart

import io.reactivex.Maybe

interface DartStationCacheResource {

    fun selectStops(): Maybe<List<DartStationEntity>>

    fun insertStops(stops: Pair<List<DartStationLocationEntity>, List<DartStationServiceEntity>>)

}

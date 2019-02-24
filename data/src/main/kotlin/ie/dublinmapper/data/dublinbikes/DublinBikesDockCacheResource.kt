package ie.dublinmapper.data.dublinbikes

import io.reactivex.Maybe

interface DublinBikesDockCacheResource {

    fun selectDocks(): Maybe<List<DublinBikesDockEntity>>

    fun insertDocks(docks: Pair<List<DublinBikesDockLocationEntity>, List<DublinBikesDockServiceEntity>>)

}

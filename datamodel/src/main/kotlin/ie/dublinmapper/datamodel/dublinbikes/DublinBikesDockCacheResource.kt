package ie.dublinmapper.datamodel.dublinbikes

import io.reactivex.Maybe

interface DublinBikesDockCacheResource {

    fun selectDocks(): Maybe<List<DublinBikesDockEntity>>

    fun insertDocks(docks: List<DublinBikesDockEntity>)

}

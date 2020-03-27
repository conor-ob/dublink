package ie.dublinmapper.datamodel

import io.reactivex.Observable
import io.rtpi.api.DublinBikesDock

interface DublinBikesDockLocalResource {

    fun selectDocks(): Observable<List<DublinBikesDock>>

    fun insertDocks(docks: List<DublinBikesDock>)
}

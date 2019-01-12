package ie.dublinmapper.view.livedata

import ie.dublinmapper.domain.model.LiveData
import ie.dublinmapper.domain.repository.Mapper
import ie.dublinmapper.model.LiveDataUi
import ie.dublinmapper.model.ServiceLocationUi

class LiveDataMapper(
    private val serviceLocation: ServiceLocationUi
) : Mapper<LiveData, LiveDataUi> {

    override fun map(from: List<LiveData>): List<LiveDataUi> {
        val liveDataUi = mutableListOf<LiveDataUi>(LiveDataUi.Header(serviceLocation))
        from.forEach { liveDataUi.add(map(it)) }
        return liveDataUi
    }

    override fun map(from: LiveData): LiveDataUi {
        return when (from) {
            is LiveData.Dart -> LiveDataUi.Dart(from)
            is LiveData.DublinBikes -> LiveDataUi.DublinBikes(from)
            is LiveData.DublinBus -> LiveDataUi.DublinBus(from)
            is LiveData.Luas -> LiveDataUi.Luas(from)
        }
    }

}

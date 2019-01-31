package ie.dublinmapper.view.livedata

import ie.dublinmapper.domain.model.*
import ie.dublinmapper.domain.repository.Mapper
import ie.dublinmapper.model.*
import java.lang.UnsupportedOperationException

object LiveDataMapper : Mapper<LiveData, LiveDataUi> {

    override fun map(from: LiveData): LiveDataUi {
        return when (from) {
            is AircoachLiveData -> throw UnsupportedOperationException()
            is DartLiveDataHeader -> DartLiveDataHeaderUi(from)
            is DartLiveData -> DartLiveDataUi(from)
            is DublinBikesLiveData -> DublinBikesLiveDataUi(from)
            is DublinBusLiveData -> DublinBusLiveDataUi(from)
            is LuasLiveData -> LuasLiveDataUi(from)
        }
    }

}

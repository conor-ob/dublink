package ie.dublinmapper.view.livedata

import ie.dublinmapper.domain.model.*
import ie.dublinmapper.domain.repository.Mapper
import ie.dublinmapper.model.ServiceLocationUi

object NearbyMapper : Mapper<ServiceLocation, ServiceLocationUi> {

    override fun map(from: ServiceLocation): ServiceLocationUi {
        return when (from) {
            is DartStation -> ServiceLocationUi.Dart(from)
            is DublinBikesDock -> ServiceLocationUi.DublinBikes(from)
            is DublinBusStop -> ServiceLocationUi.DublinBus(from)
            is LuasStop -> ServiceLocationUi.Luas(from)
        }
    }

}

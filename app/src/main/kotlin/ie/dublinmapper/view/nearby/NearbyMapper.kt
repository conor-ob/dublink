package ie.dublinmapper.view.nearby

import ie.dublinmapper.domain.model.*
import ie.dublinmapper.domain.repository.Mapper
import ie.dublinmapper.model.*

object NearbyMapper : Mapper<ServiceLocation, ServiceLocationUi> {

    override fun map(from: ServiceLocation): ServiceLocationUi {
        return when (from) {
            is AircoachStop -> AircoachStopUi(from)
            is DartStation -> DartStationUi(from)
            is DublinBikesDock -> DublinBikesDockUi(from)
            is DublinBusStop -> DublinBusStopUi(from)
            is LuasStop -> LuasStopUi(from)
            is SwordsExpressStop -> SwordsExpressStopUi(from)
        }
    }

}

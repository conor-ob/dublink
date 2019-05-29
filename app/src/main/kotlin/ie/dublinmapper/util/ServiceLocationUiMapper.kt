package ie.dublinmapper.util

import ie.dublinmapper.domain.model.AircoachStop
import ie.dublinmapper.domain.model.BusEireannStop
import ie.dublinmapper.domain.model.DartStation
import ie.dublinmapper.domain.model.DublinBikesDock
import ie.dublinmapper.domain.model.DublinBusStop
import ie.dublinmapper.domain.model.LuasStop
import ie.dublinmapper.domain.model.ServiceLocation
import ie.dublinmapper.domain.model.SwordsExpressStop
import ie.dublinmapper.domain.repository.Mapper
import ie.dublinmapper.model.AircoachStopUi
import ie.dublinmapper.model.BusEireannStopUi
import ie.dublinmapper.model.DartStationUi
import ie.dublinmapper.model.DublinBikesDockUi
import ie.dublinmapper.model.DublinBusStopUi
import ie.dublinmapper.model.LuasStopUi
import ie.dublinmapper.model.ServiceLocationUi
import ie.dublinmapper.model.SwordsExpressStopUi

object ServiceLocationUiMapper : Mapper<ServiceLocation, ServiceLocationUi> {

    override fun map(from: ServiceLocation): ServiceLocationUi {
        return when (from) {
            is AircoachStop -> AircoachStopUi(from)
            is BusEireannStop -> BusEireannStopUi(from)
            is DartStation -> DartStationUi(from)
            is DublinBikesDock -> DublinBikesDockUi(from)
            is DublinBusStop -> DublinBusStopUi(from)
            is LuasStop -> LuasStopUi(from)
            is SwordsExpressStop -> SwordsExpressStopUi(from)
        }
    }

}

package ie.dublinmapper.model

import ie.dublinmapper.R
import ie.dublinmapper.domain.model.*
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.Operator
import java.util.*

sealed class ServiceLocationUi(
    open val serviceLocation: ServiceLocation,
    open val mapIconText: String,
    open val colourId: Int,
    val id: String = serviceLocation.id,
    val name: String = serviceLocation.name,
    val coordinate: Coordinate = serviceLocation.coordinate,
    val operators: EnumSet<Operator> = serviceLocation.operators
) {

//    override fun equals(other: Any?): Boolean {
//        if (other == null) {
//            return false
//        }
//        if (other is ServiceLocationUi) {
//            return serviceLocation == other.serviceLocation
//        }
//        return false
//    }
//
//    override fun hashCode(): Int {
//        return Objects.hashCode(serviceLocation)
//    }

}

data class AircoachStopUi(
    val aircoachStop: AircoachStop
) : ServiceLocationUi(
    aircoachStop,
    aircoachStop.name,
    R.color.dublinBusBlue
)

data class DartStationUi(
    val dartStation: DartStation
) : ServiceLocationUi(
    dartStation,
    dartStation.name,
    R.color.dartGreen
)

data class DublinBikesDockUi(
    val dublinBikesDock: DublinBikesDock
) : ServiceLocationUi(
    dublinBikesDock,
    dublinBikesDock.availableBikes.toString(),
    R.color.dublinBikesTeal
)


data class DublinBusStopUi(
    val dublinBusStop: DublinBusStop
) : ServiceLocationUi(
    dublinBusStop,
    dublinBusStop.id,
    R.color.commuterBlue
)

data class LuasStopUi(
    val luasStop: LuasStop
) : ServiceLocationUi(
    luasStop,
    luasStop.name,
    R.color.luasPurple
)

data class SwordsExpressStopUi(
    val swordsExpressStop: SwordsExpressStop
) : ServiceLocationUi(
    swordsExpressStop,
    swordsExpressStop.name,
    R.color.luasGreen
)

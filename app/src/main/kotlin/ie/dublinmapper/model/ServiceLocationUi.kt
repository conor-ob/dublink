package ie.dublinmapper.model

import com.xwray.groupie.kotlinandroidextensions.Item
import ie.dublinmapper.R
import ie.dublinmapper.domain.model.*
import ie.dublinmapper.model.aircoach.AircoachStopItem
import ie.dublinmapper.model.buseireann.BusEireannStopItem
import ie.dublinmapper.model.dart.DartStationItem
import ie.dublinmapper.model.dublinbikes.DublinBikesDockItem
import ie.dublinmapper.model.dublinbus.DublinBusStopItem
import ie.dublinmapper.model.luas.LuasStopItem
import ie.dublinmapper.model.swordsexpress.SwordsExpressStopItem
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.Operator
import ie.dublinmapper.util.Service

sealed class ServiceLocationUi(
    open val serviceLocation: ServiceLocation,
    open val mapIconText: String,
    open val styleId: Int,
    val id: String = serviceLocation.id,
    val name: String = serviceLocation.name,
    val service: Service = serviceLocation.service,
    val coordinate: Coordinate = serviceLocation.coordinate,
    val operators: Set<Operator> = serviceLocation.operators
) {

    abstract fun toItem(): Item

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
    R.style.DublinMapperTheme
) {
    override fun toItem() = AircoachStopItem(aircoachStop)
}

data class BusEireannStopUi(
    val busEireannStop: BusEireannStop
) : ServiceLocationUi(
    busEireannStop,
    busEireannStop.name,
    R.style.DublinMapperTheme
) {
    override fun toItem() = BusEireannStopItem(busEireannStop)
}

data class DartStationUi(
    val dartStation: DartStation
) : ServiceLocationUi(
    dartStation,
    dartStation.name,
    R.style.DartTheme
) {
    override fun toItem() = DartStationItem(this)
}

data class DublinBikesDockUi(
    val dublinBikesDock: DublinBikesDock
) : ServiceLocationUi(
    dublinBikesDock,
    dublinBikesDock.availableBikes.toString(),
    R.style.DublinMapperTheme
) {
    override fun toItem() = DublinBikesDockItem(dublinBikesDock)
}


data class DublinBusStopUi(
    val dublinBusStop: DublinBusStop
) : ServiceLocationUi(
    dublinBusStop,
    dublinBusStop.id,
    R.style.DublinMapperTheme
) {
    override fun toItem() = DublinBusStopItem(dublinBusStop)
}

data class LuasStopUi(
    val luasStop: LuasStop
) : ServiceLocationUi(
    luasStop,
    luasStop.name,
    R.style.DublinMapperTheme
) {
    override fun toItem() = LuasStopItem(luasStop)
}

data class SwordsExpressStopUi(
    val swordsExpressStop: SwordsExpressStop
) : ServiceLocationUi(
    swordsExpressStop,
    swordsExpressStop.name,
    R.style.DublinMapperTheme
) {
    override fun toItem() = SwordsExpressStopItem(swordsExpressStop)
}

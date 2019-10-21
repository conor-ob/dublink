package ie.dublinmapper.model

import com.xwray.groupie.kotlinandroidextensions.Item
import ie.dublinmapper.ui.R
import ie.dublinmapper.domain.model.*
import ie.dublinmapper.model.aircoach.AircoachStopItem
import ie.dublinmapper.model.buseireann.BusEireannStopItem
import ie.dublinmapper.model.dart.DartStationItem
import ie.dublinmapper.model.dublinbikes.DublinBikesDockItem
import ie.dublinmapper.model.dublinbus.DublinBusStopItem
import ie.dublinmapper.model.luas.LuasStopItem

//sealed class ServiceLocationUi(
//    open val serviceLocation: ServiceLocation,
//    open val mapIconText: String,
//    open val styleId: Int,
//    val serviceId: String = serviceLocation.serviceId,
//    val name: String = serviceLocation.name,
//    val service: Service = serviceLocation.service,
//    val coordinate: Coordinate = serviceLocation.coordinate,
//    val operators: Set<Operator> = serviceLocation.operators
//) {
//
//    abstract fun toItem(isEven: Boolean, isLast: Boolean): Item
//
////    override fun equals(other: Any?): Boolean {
////        if (other == null) {
////            return false
////        }
////        if (other is ServiceLocationUi) {
////            return serviceLocation == other.serviceLocation
////        }
////        return false
////    }
////
////    override fun hashCode(): Int {
////        return Objects.hashCode(serviceLocation)
////    }
//
//}
//
//data class AircoachStopUi(
//    val aircoachStop: AircoachStop
//) : ServiceLocationUi(
//    aircoachStop,
//    aircoachStop.name,
//    R.style.AircoachTheme
//) {
//    override fun toItem(isEven: Boolean, isLast: Boolean) = AircoachStopItem(this, isEven, isLast)
//}
//
//data class BusEireannStopUi(
//    val busEireannStop: BusEireannStop
//) : ServiceLocationUi(
//    busEireannStop,
//    busEireannStop.name,
//    R.style.BusEireannTheme
//) {
//    override fun toItem(isEven: Boolean, isLast: Boolean) = BusEireannStopItem(this, isEven, isLast)
//}
//
//data class DartStationUi(
//    val dartStation: DartStation
//) : ServiceLocationUi(
//    dartStation,
//    dartStation.name,
//    R.style.DartTheme
//) {
//    override fun toItem(isEven: Boolean, isLast: Boolean) = DartStationItem(this, isEven, isLast)
//}
//
//data class DublinBikesDockUi(
//    val dublinBikesDock: DublinBikesDock
//) : ServiceLocationUi(
//    dublinBikesDock,
//    dublinBikesDock.availableBikes.toString(),
//    R.style.DublinMapperTheme
//) {
//    override fun toItem(isEven: Boolean, isLast: Boolean) = DublinBikesDockItem(dublinBikesDock)
//}
//
//
//data class DublinBusStopUi(
//    val dublinBusStop: DublinBusStop
//) : ServiceLocationUi(
//    dublinBusStop,
//    dublinBusStop.serviceId,
//    R.style.DublinBusTheme
//) {
//    override fun toItem(isEven: Boolean, isLast: Boolean) = DublinBusStopItem(this, isEven, isLast)
//}
//
//data class LuasStopUi(
//    val luasStop: LuasStop
//) : ServiceLocationUi(
//    luasStop,
//    luasStop.name,
//    R.style.LuasTheme
//) {
//    override fun toItem(isEven: Boolean, isLast: Boolean) = LuasStopItem(this, isEven, isLast)
//}
//
//data class SwordsExpressStopUi(
//    val swordsExpressStop: SwordsExpressStop
//) : ServiceLocationUi(
//    swordsExpressStop,
//    swordsExpressStop.name,
//    R.style.DublinMapperTheme
//) {
//    override fun toItem(isEven: Boolean, isLast: Boolean) = SwordsExpressStopItem(swordsExpressStop)
//}

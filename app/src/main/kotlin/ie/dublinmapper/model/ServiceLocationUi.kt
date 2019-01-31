package ie.dublinmapper.model

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.R
import ie.dublinmapper.domain.model.*
import ie.dublinmapper.model.aircoach.AircoachStopItem
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.Operator
import kotlinx.android.synthetic.main.view_nearby_list_item_dart_header.*
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
    R.color.dublinBusBlue
) {
    override fun toItem() = AircoachStopItem(aircoachStop)
}

data class DartStationUi(
    val dartStation: DartStation
) : ServiceLocationUi(
    dartStation,
    dartStation.name,
    R.color.dartGreen
) {
    override fun toItem() = DartStationItem(dartStation)
}

data class DublinBikesDockUi(
    val dublinBikesDock: DublinBikesDock
) : ServiceLocationUi(
    dublinBikesDock,
    dublinBikesDock.availableBikes.toString(),
    R.color.dublinBikesTeal
) {
    override fun toItem() = DublinBikesDockItem(dublinBikesDock)
}


data class DublinBusStopUi(
    val dublinBusStop: DublinBusStop
) : ServiceLocationUi(
    dublinBusStop,
    dublinBusStop.id,
    R.color.commuterBlue
) {
    override fun toItem() = DublinBusStopItem(dublinBusStop)
}

data class LuasStopUi(
    val luasStop: LuasStop
) : ServiceLocationUi(
    luasStop,
    luasStop.name,
    R.color.luasPurple
) {
    override fun toItem() = LuasStopItem(luasStop)
}

data class SwordsExpressStopUi(
    val swordsExpressStop: SwordsExpressStop
) : ServiceLocationUi(
    swordsExpressStop,
    swordsExpressStop.name,
    R.color.luasGreen
) {
    override fun toItem() = SwordsExpressStopItem(swordsExpressStop)
}

class DartStationItem(
    private val dartStation: DartStation
) : Item() {

    override fun getLayout() = R.layout.view_nearby_list_item_dart_header

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.service_location_name.text = dartStation.name
    }

}

class DublinBikesDockItem(
    private val dublinBikesDock: DublinBikesDock
) : Item() {

    override fun getLayout(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

class DublinBusStopItem(
    private val dublinBusStop: DublinBusStop
) : Item() {

    override fun getLayout(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

class LuasStopItem(
    private val luasStop: LuasStop
) : Item() {

    override fun getLayout(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

class SwordsExpressStopItem(
    private val swordsExpressStop: SwordsExpressStop
) : Item() {

    override fun getLayout(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

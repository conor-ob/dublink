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

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        if (other is ServiceLocationUi) {
            return serviceLocation == other.serviceLocation
        }
        return false
    }

    override fun hashCode(): Int {
        return Objects.hashCode(serviceLocation)
    }

    class Dart(
        dartStation: DartStation
    ) : ServiceLocationUi(
        dartStation,
        dartStation.name,
        R.color.dartGreen
    )

    class DublinBikes(
        dublinBikesDock: DublinBikesDock
    ) : ServiceLocationUi(
        dublinBikesDock,
        dublinBikesDock.availableBikes.toString(),
        R.color.dublinBikesTeal
    )

    class DublinBus(
        dublinBusStop: DublinBusStop
    ) : ServiceLocationUi(
        dublinBusStop,
        dublinBusStop.id,
        R.color.commuterBlue
    )

    class Luas(
        luasStop: LuasStop
    ) : ServiceLocationUi(
        luasStop,
        luasStop.name,
        R.color.luasPurple
    )

}
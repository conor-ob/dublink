package ie.dublinmapper.model

import ie.dublinmapper.R
import ie.dublinmapper.domain.model.*

sealed class ServiceLocationUi(
    open val serviceLocation: ServiceLocation,
    open val mapIconText: String,
    open val colourId: Int
) {

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
        R.color.dublinBusYellow
    )

    class Luas(
        luasStop: LuasStop
    ) : ServiceLocationUi(
        luasStop,
        luasStop.name,
        R.color.luasPurple
    )

}

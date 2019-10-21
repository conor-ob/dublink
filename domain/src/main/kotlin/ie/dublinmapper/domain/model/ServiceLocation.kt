package ie.dublinmapper.domain.model

import io.rtpi.api.*

interface DetailedServiceLocation : ServiceLocation {

    val serviceLocationName: String

    val isFavourite: Boolean

}

abstract class AbstractDetailedServiceLocation(
    serviceLocation: ServiceLocation,
    private val favourite: Favourite?
) : DetailedServiceLocation {
    override val id = serviceLocation.id
    override val serviceLocationName = serviceLocation.name
    override val coordinate = serviceLocation.coordinate
    override val operators = serviceLocation.operators
    override val service = serviceLocation.service
    override val name get() = favourite?.name ?: serviceLocationName
    override val isFavourite = favourite != null
}

data class DetailedAircoachStop(
    private val aircoachStop: AircoachStop,
    private val favourite: Favourite? = null
) : AbstractDetailedServiceLocation(aircoachStop, favourite) {
    val routes = aircoachStop.routes
}

data class DetailedBusEireannStop(
    private val busEireannStop: BusEireannStop,
    private val favourite: Favourite? = null
) : AbstractDetailedServiceLocation(busEireannStop, favourite) {
    val routes = busEireannStop.routes
}

data class DetailedIrishRailStation(
    private val irishRailStation: IrishRailStation,
    private val favourite: Favourite? = null
) : AbstractDetailedServiceLocation(irishRailStation, favourite)

data class DetailedDublinBikesDock(
    private val dublinBikesDock: DublinBikesDock,
    private val favourite: Favourite? = null
) : AbstractDetailedServiceLocation(dublinBikesDock, favourite) {
    val docks = dublinBikesDock.docks
    val availableBikes = dublinBikesDock.availableBikes
    val availableDocks = dublinBikesDock.availableDocks
}

data class DetailedDublinBusStop(
    private val dublinBusStop: DublinBusStop,
    private val favourite: Favourite? = null
) : AbstractDetailedServiceLocation(dublinBusStop, favourite) {
    val routes = dublinBusStop.routes
}

data class DetailedLuasStop(
    private val luasStop: LuasStop,
    private val favourite: Favourite? = null
) : AbstractDetailedServiceLocation(luasStop, favourite) {
    val routes = luasStop.routes
}

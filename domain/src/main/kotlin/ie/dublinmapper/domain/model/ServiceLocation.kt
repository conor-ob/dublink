package ie.dublinmapper.domain.model

import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.Operator
import ie.dublinmapper.util.Service

interface ServiceLocation {

    val id: String

    val name: String

    val coordinate: Coordinate

    val service: Service

    val operators: Set<Operator>

}

data class AircoachStop(
    override val id: String,
    override val name: String,
    override val coordinate: Coordinate,
    override val service: Service,
    override val operators: Set<Operator>,
    val routes: Map<Operator, List<String>>
) : ServiceLocation

data class BusEireannStop(
    override val id: String,
    override val name: String,
    override val coordinate: Coordinate,
    override val service: Service,
    override val operators: Set<Operator>,
    val routes: Map<Operator, List<String>>
) : ServiceLocation

data class DartStation(
    override val id: String,
    override val name: String,
    override val coordinate: Coordinate,
    override val service: Service,
    override val operators: Set<Operator>
) : ServiceLocation

data class DublinBikesDock(
    override val id: String,
    override val name: String,
    override val coordinate: Coordinate,
    override val service: Service,
    override val operators: Set<Operator>,
    val docks: Int,
    val availableBikes: Int,
    val availableDocks: Int
) : ServiceLocation

data class DublinBusStop(
    override val id: String,
    override val name: String,
    override val coordinate: Coordinate,
    override val service: Service,
    override val operators: Set<Operator>,
    val routes: Map<Operator, List<String>>
) : ServiceLocation

data class LuasStop(
    override val id: String,
    override val name: String,
    override val coordinate: Coordinate,
    override val service: Service,
    override val operators: Set<Operator>,
    val routes: Map<Operator, List<String>>
) : ServiceLocation

data class SwordsExpressStop(
    override val id: String,
    override val name: String,
    override val coordinate: Coordinate,
    override val service: Service,
    override val operators: Set<Operator>,
    val direction: String
) : ServiceLocation

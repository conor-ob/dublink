package ie.dublinmapper.domain.model

import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.Operator
import ie.dublinmapper.util.Service
import java.util.*

sealed class ServiceLocation(
    open val id: String,
    open val name: String,
    open val service: Service,
    open val coordinate: Coordinate,
    open val operators: EnumSet<Operator>
)

data class AircoachStop(
    override val id: String,
    override val name: String,
    override val coordinate: Coordinate,
    override val operators: EnumSet<Operator>,
    val routes: Map<Operator, Set<String>>
) : ServiceLocation(id, name, Service.AIRCOACH, coordinate, operators)

data class BusEireannStop(
    override val id: String,
    override val name: String,
    override val coordinate: Coordinate,
    override val operators: EnumSet<Operator>,
    val routes: Map<Operator, Set<String>>
) : ServiceLocation(id, name, Service.BUS_EIREANN, coordinate, operators)

data class DartStation(
    override val id: String,
    override val name: String,
    override val coordinate: Coordinate,
    override val operators: EnumSet<Operator>
) : ServiceLocation(id, name, Service.IRISH_RAIL, coordinate, operators)

data class DublinBikesDock(
    override val id: String,
    override val name: String,
    override val coordinate: Coordinate,
    override val operators: EnumSet<Operator> = Operator.dublinBikes(),
    val docks: Int,
    val availableBikes: Int,
    val availableDocks: Int
) : ServiceLocation(id, name, Service.DUBLIN_BIKES, coordinate, operators)

data class DublinBusStop(
    override val id: String,
    override val name: String,
    override val coordinate: Coordinate,
    override val operators: EnumSet<Operator>,
    val routes: Map<Operator, Set<String>>
) : ServiceLocation(id, name, Service.DUBLIN_BUS, coordinate, operators)

data class LuasStop(
    override val id: String,
    override val name: String,
    override val coordinate: Coordinate,
    override val operators: EnumSet<Operator>,
    val routes: Map<Operator, Set<String>>
) : ServiceLocation(id, name, Service.LUAS, coordinate, operators)

data class SwordsExpressStop(
    override val id: String,
    override val name: String,
    override val coordinate: Coordinate,
    override val operators: EnumSet<Operator> = Operator.swordsExpress(),
    val direction: String
) : ServiceLocation(id,name, Service.SWORDS_EXPRESS, coordinate, operators)

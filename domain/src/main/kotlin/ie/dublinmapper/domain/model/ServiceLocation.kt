package ie.dublinmapper.domain.model

import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.Operator
import java.util.*

sealed class ServiceLocation(
    open val id: String,
    open val name: String,
    open val coordinate: Coordinate,
    open val operators: EnumSet<Operator>
)

data class DartStation(
    override val id: String,
    override val name: String,
    override val coordinate: Coordinate,
    override val operators: EnumSet<Operator>
) : ServiceLocation(id, name, coordinate, operators)

data class DublinBikesDock(
    override val id: String,
    override val name: String,
    override val coordinate: Coordinate,
    override val operators: EnumSet<Operator> = Operator.dublinBikes(),
    val availableBikes: Int
) : ServiceLocation(id, name, coordinate, operators)

data class DublinBusStop(
    override val id: String,
    override val name: String,
    override val coordinate: Coordinate,
    override val operators: EnumSet<Operator>,
    val routes: Map<Operator, Set<String>>
) : ServiceLocation(id, name, coordinate, operators)

data class LuasStop(
    override val id: String,
    override val name: String,
    override val coordinate: Coordinate,
    override val operators: EnumSet<Operator> = Operator.luas()
) : ServiceLocation(id, name, coordinate, operators)

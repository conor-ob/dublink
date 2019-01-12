package ie.dublinmapper.domain.model

import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.Operator
import java.util.*

sealed class ServiceLocation(
    open val id: String,
    open val name: String,
    open val coordinate: Coordinate,
    open val operators: EnumSet<Operator>,
    open val routes: Map<Operator, Set<String>>
)

data class DartStation(
    override val id: String,
    override val name: String,
    override val coordinate: Coordinate,
    override val operators: EnumSet<Operator> = Operator.dart(),
    override val routes: Map<Operator, Set<String>> = emptyMap()
    ) : ServiceLocation(id, name, coordinate, operators, routes)

data class DublinBikesDock(
    override val id: String,
    override val name: String,
    override val coordinate: Coordinate,
    override val operators: EnumSet<Operator> = Operator.dublinBikes(),
    override val routes: Map<Operator, Set<String>> = emptyMap(),
    val availableBikes: Int
    ) : ServiceLocation(id, name, coordinate, operators, routes)

data class DublinBusStop(
    override val id: String,
    override val name: String,
    override val coordinate: Coordinate,
    override val operators: EnumSet<Operator> = Operator.dublinBus(),
    override val routes: Map<Operator, Set<String>> = emptyMap()
    ) : ServiceLocation(id, name, coordinate, operators, routes)

data class LuasStop(
    override val id: String,
    override val name: String,
    override val coordinate: Coordinate,
    override val operators: EnumSet<Operator> = Operator.luas(),
    override val routes: Map<Operator, Set<String>> = emptyMap()
) : ServiceLocation(id, name, coordinate, operators, routes)

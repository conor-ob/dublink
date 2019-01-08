package ie.dublinmapper.domain.model

import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.Operator
import java.util.*

interface ServiceLocation {

    val id: String

    val name: String

    val coordinate: Coordinate

    val operators: EnumSet<Operator>

    val routes: Map<Operator, Set<String>>

    val mapIconText: String

    //TODO convert to ServiceLocationMapMarker and inject icon map?

}

data class DartStation(
    override val id: String,
    override val name: String,
    override val coordinate: Coordinate,
    override val operators: EnumSet<Operator> = Operator.dart(),
    override val routes: Map<Operator, Set<String>> = emptyMap(),
    override val mapIconText: String = name
) : ServiceLocation

data class DublinBikesDock(
    override val id: String,
    override val name: String,
    override val coordinate: Coordinate,
    override val operators: EnumSet<Operator> = Operator.dublinBikes(),
    override val routes: Map<Operator, Set<String>> = emptyMap(),
    override val mapIconText: String
) : ServiceLocation

data class DublinBusStop(
    override val id: String,
    override val name: String,
    override val coordinate: Coordinate,
    override val operators: EnumSet<Operator> = Operator.dublinBus(),
    override val routes: Map<Operator, Set<String>> = emptyMap(),
    override val mapIconText: String = id
) : ServiceLocation

data class LuasStop(
    override val id: String,
    override val name: String,
    override val coordinate: Coordinate,
    override val operators: EnumSet<Operator> = Operator.luas(),
    override val routes: Map<Operator, Set<String>> = emptyMap(),
    override val mapIconText: String = name
) : ServiceLocation

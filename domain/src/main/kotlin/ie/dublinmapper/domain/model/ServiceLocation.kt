package ie.dublinmapper.domain.model

import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.Operator
import ie.dublinmapper.util.Service

interface ServiceLocation {

    val id: String

    val name: String

    val serviceLocationName: String

    val coordinate: Coordinate

    val service: Service

    val operators: Set<Operator>

    fun isFavourite(): Boolean

}

abstract class AbstractServiceLocation(
    private val favourite: Favourite?
) : ServiceLocation {

    override val name get() = favourite?.name ?: serviceLocationName

    override fun isFavourite() = favourite != null
    
}

data class AircoachStop(
    override val id: String,
    override val serviceLocationName: String,
    override val coordinate: Coordinate,
    override val service: Service,
    override val operators: Set<Operator>,
    private val favourite: Favourite? = null,
    val routes: Map<Operator, List<String>>
) : AbstractServiceLocation(favourite)

data class BusEireannStop(
    override val id: String,
    override val serviceLocationName: String,
    override val coordinate: Coordinate,
    override val service: Service,
    override val operators: Set<Operator>,
    private val favourite: Favourite? = null,
    val routes: Map<Operator, List<String>>
) : AbstractServiceLocation(favourite)

data class IrishRailStation(
    override val id: String,
    override val serviceLocationName: String,
    override val coordinate: Coordinate,
    override val service: Service,
    override val operators: Set<Operator>,
    private val favourite: Favourite? = null
) : AbstractServiceLocation(favourite)

data class DublinBikesDock(
    override val id: String,
    override val serviceLocationName: String,
    override val coordinate: Coordinate,
    override val service: Service,
    override val operators: Set<Operator>,
    private val favourite: Favourite? = null,
    val docks: Int,
    val availableBikes: Int,
    val availableDocks: Int
) : AbstractServiceLocation(favourite)

data class DublinBusStop(
    override val id: String,
    override val serviceLocationName: String,
    override val coordinate: Coordinate,
    override val service: Service,
    override val operators: Set<Operator>,
    private val favourite: Favourite? = null,
    val routes: Map<Operator, List<String>>
) : AbstractServiceLocation(favourite)

data class LuasStop(
    override val id: String,
    override val serviceLocationName: String,
    override val coordinate: Coordinate,
    override val service: Service,
    override val operators: Set<Operator>,
    private val favourite: Favourite? = null,
    val routes: Map<Operator, List<String>>
) : AbstractServiceLocation(favourite)

data class SwordsExpressStop(
    override val id: String,
    override val serviceLocationName: String,
    override val coordinate: Coordinate,
    override val service: Service,
    override val operators: Set<Operator>,
    private val favourite: Favourite? = null,
    val direction: String
) : AbstractServiceLocation(favourite)

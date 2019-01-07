package ie.dublinmapper.domain.model.dublinbikes

import ie.dublinmapper.domain.model.ServiceLocation
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.Operator

data class DublinBikesDock(
    override val id: String,
    override val name: String,
    override val coordinate: Coordinate,
    override val operator: Operator,
    override val mapIconText: String
) : ServiceLocation

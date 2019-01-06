package ie.dublinmapper.domain.model.luas

import ie.dublinmapper.domain.model.ServiceLocation
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.Operator

data class LuasStop(
    override val id: String,
    override val name: String,
    override val coordinate: Coordinate,
    override val operator: Operator
) : ServiceLocation

package ie.dublinmapper.domain.model

import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.Operator

interface ServiceLocation {

    val id: String

    val name: String

    val coordinate: Coordinate

    val operator: Operator

    val mapIconText: String

}

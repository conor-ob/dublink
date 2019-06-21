package ie.dublinmapper.domain.model

import ie.dublinmapper.util.Operator
import ie.dublinmapper.util.Service

data class Favourite(
    val id: String,
    val name: String,
    val service: Service,
    val routes: Map<Operator, List<String>>
)

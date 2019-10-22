package ie.dublinmapper.domain.model

import io.rtpi.api.Operator
import io.rtpi.api.Service

data class Favourite(
    val id: String,
    val name: String,
    val service: Service,
    val order: Long,
    val routes: Map<Operator, List<String>>
)

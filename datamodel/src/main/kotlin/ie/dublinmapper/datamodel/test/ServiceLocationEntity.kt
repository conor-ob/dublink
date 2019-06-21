package ie.dublinmapper.datamodel.test

import androidx.room.Embedded
import androidx.room.Relation

data class ServiceLocationEntity(
    @field:Embedded val location: LocationEntity
) {
    @field:Relation(parentColumn = "id", entityColumn = "location_id")
    var services: List<ServiceEntity> = emptyList()
}

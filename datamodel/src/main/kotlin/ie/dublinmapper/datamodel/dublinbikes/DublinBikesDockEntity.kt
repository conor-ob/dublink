package ie.dublinmapper.datamodel.dublinbikes

import androidx.room.Embedded
import androidx.room.Relation

data class DublinBikesDockEntity(
    @field:Embedded val location: DublinBikesDockLocationEntity
) {
    @field:Relation(parentColumn = "id", entityColumn = "dock_id")
    var services: List<DublinBikesDockServiceEntity> = emptyList()
}

package ie.dublinmapper.datamodel.swordsexpress

import androidx.room.Embedded
import androidx.room.Relation

data class SwordsExpressStopEntity(
    @field:Embedded val location: SwordsExpressStopLocationEntity
) {
    @field:Relation(parentColumn = "id", entityColumn = "stop_id")
    var services: List<SwordsExpressStopServiceEntity> = emptyList()
}

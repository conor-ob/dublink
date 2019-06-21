package ie.dublinmapper.datamodel.luas

import androidx.room.Embedded
import androidx.room.Relation

data class LuasStopEntity(
    @field:Embedded val location: LuasStopLocationEntity
) {
    @field:Relation(parentColumn = "id", entityColumn = "stop_id")
    var services: List<LuasStopServiceEntity> = emptyList()
}

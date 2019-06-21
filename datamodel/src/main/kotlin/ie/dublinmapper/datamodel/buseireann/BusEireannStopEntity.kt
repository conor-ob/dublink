package ie.dublinmapper.datamodel.buseireann

import androidx.room.Embedded
import androidx.room.Relation

data class BusEireannStopEntity(
    @field:Embedded val location: BusEireannStopLocationEntity
) {
    @field:Relation(parentColumn = "id", entityColumn = "stop_id")
    var services: List<BusEireannStopServiceEntity> = emptyList()
}

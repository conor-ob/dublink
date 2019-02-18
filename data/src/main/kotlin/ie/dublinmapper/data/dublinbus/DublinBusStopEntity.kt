package ie.dublinmapper.data.dublinbus

import androidx.room.Embedded
import androidx.room.Relation

data class DublinBusStopEntity(
    @field:Embedded val location: DublinBusStopLocationEntity
) {
    @field:Relation(parentColumn = "id", entityColumn = "id")
    var services: List<DublinBusStopServiceEntity> = emptyList()
}

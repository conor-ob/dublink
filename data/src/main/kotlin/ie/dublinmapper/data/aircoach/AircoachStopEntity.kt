package ie.dublinmapper.data.aircoach

import androidx.room.Embedded
import androidx.room.Relation

data class AircoachStopEntity(
    @field:Embedded val location: AircoachStopLocationEntity
) {
    @field:Relation(parentColumn = "id", entityColumn = "id")
    var services: List<AircoachStopServiceEntity> = emptyList()
}

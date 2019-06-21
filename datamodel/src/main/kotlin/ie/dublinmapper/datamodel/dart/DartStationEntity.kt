package ie.dublinmapper.datamodel.dart

import androidx.room.Embedded
import androidx.room.Relation

data class DartStationEntity(
    @field:Embedded val location: DartStationLocationEntity
) {
    @field:Relation(parentColumn = "id", entityColumn = "station_id")
    var services: List<DartStationServiceEntity> = emptyList()
}

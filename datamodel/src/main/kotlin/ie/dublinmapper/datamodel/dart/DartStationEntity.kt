package ie.dublinmapper.datamodel.dart

import androidx.room.*

data class DartStationEntity(
    @field:Embedded val location: DartStationLocationEntity
) {
    @field:Relation(parentColumn = "id", entityColumn = "station_id")
    var services: List<DartStationServiceEntity> = emptyList()
}

@Entity(tableName = "dart_station_locations")
data class DartStationLocationEntity(
    @field:PrimaryKey @field:ColumnInfo(name = "id") val id: String,
    @field:ColumnInfo(name = "name") val name: String,
    @field:ColumnInfo(name = "latitude") val latitude: Double,
    @field:ColumnInfo(name = "longitude") val longitude: Double
)

@Entity(
    tableName = "dart_station_services",
    indices = [Index("station_id")],
    foreignKeys = [
        ForeignKey(
            entity = DartStationLocationEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("station_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DartStationServiceEntity(
    @field:PrimaryKey(autoGenerate = true) @field:ColumnInfo(name = "id") val id: Int = 0,
    @field:ColumnInfo(name = "station_id") val stationId: String,
    @field:ColumnInfo(name = "operator") val operator: String
)

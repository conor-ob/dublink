package ie.dublinmapper.data.dart

import androidx.room.*

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
    @field:ColumnInfo(name = "operator") val operator: String,
    @field:ColumnInfo(name = "route") val route: String
)

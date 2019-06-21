package ie.dublinmapper.datamodel.aircoach

import androidx.room.*

data class AircoachStopEntity(
    @field:Embedded val location: AircoachStopLocationEntity
) {
    @field:Relation(parentColumn = "id", entityColumn = "stop_id")
    var services: List<AircoachStopServiceEntity> = emptyList()
}

@Entity(tableName = "aircoach_stop_locations")
data class AircoachStopLocationEntity(
    @field:PrimaryKey @field:ColumnInfo(name = "id") val id: String,
    @field:ColumnInfo(name = "name") val name: String,
    @field:ColumnInfo(name = "latitude") val latitude: Double,
    @field:ColumnInfo(name = "longitude") val longitude: Double
)

@Entity(
    tableName = "aircoach_stop_services",
    indices = [Index("stop_id")],
    foreignKeys = [
        ForeignKey(
            entity = AircoachStopLocationEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("stop_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AircoachStopServiceEntity(
    @field:PrimaryKey(autoGenerate = true) @field:ColumnInfo(name = "id") val id: Int = 0,
    @field:ColumnInfo(name = "stop_id") val stopId: String,
    @field:ColumnInfo(name = "operator") val operator: String,
    @field:ColumnInfo(name = "route") val route: String
)

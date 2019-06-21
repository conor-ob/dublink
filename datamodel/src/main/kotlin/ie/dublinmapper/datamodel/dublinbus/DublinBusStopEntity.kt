package ie.dublinmapper.datamodel.dublinbus

import androidx.room.*

data class DublinBusStopEntity(
    @field:Embedded val location: DublinBusStopLocationEntity
) {
    @field:Relation(parentColumn = "id", entityColumn = "stop_id")
    var services: List<DublinBusStopServiceEntity> = emptyList()
}

@Entity(tableName = "dublinbus_stop_locations")
data class DublinBusStopLocationEntity(
    @field:PrimaryKey @field:ColumnInfo(name = "id") val id: String,
    @field:ColumnInfo(name = "name") val name: String,
    @field:ColumnInfo(name = "latitude") val latitude: Double,
    @field:ColumnInfo(name = "longitude") val longitude: Double
)

@Entity(
    tableName = "dublinbus_stop_services",
    indices = [Index("stop_id")],
    foreignKeys = [
        ForeignKey(
            entity = DublinBusStopLocationEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("stop_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DublinBusStopServiceEntity(
    @field:PrimaryKey(autoGenerate = true) @field:ColumnInfo(name = "id") val id: Int = 0,
    @field:ColumnInfo(name = "stop_id") val stopId: String,
    @field:ColumnInfo(name = "operator") val operator: String,
    @field:ColumnInfo(name = "route") val route: String
)

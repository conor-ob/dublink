package ie.dublinmapper.datamodel.buseireann

import androidx.room.*

data class BusEireannStopEntity(
    @field:Embedded val location: BusEireannStopLocationEntity
) {
    @field:Relation(parentColumn = "id", entityColumn = "stop_id")
    var services: List<BusEireannStopServiceEntity> = emptyList()
}

@Entity(tableName = "buseireann_stop_locations")
data class BusEireannStopLocationEntity(
    @field:PrimaryKey @field:ColumnInfo(name = "id") val id: String,
    @field:ColumnInfo(name = "name") val name: String,
    @field:ColumnInfo(name = "latitude") val latitude: Double,
    @field:ColumnInfo(name = "longitude") val longitude: Double
)

@Entity(
    tableName = "buseireann_stop_services",
    indices = [Index("stop_id")],
    foreignKeys = [
        ForeignKey(
            entity = BusEireannStopLocationEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("stop_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class BusEireannStopServiceEntity(
    @field:PrimaryKey(autoGenerate = true) @field:ColumnInfo(name = "serviceId") val id: Int = 0,
    @field:ColumnInfo(name = "stop_id") val stopId: String,
    @field:ColumnInfo(name = "operator") val operator: String,
    @field:ColumnInfo(name = "route") val route: String
)

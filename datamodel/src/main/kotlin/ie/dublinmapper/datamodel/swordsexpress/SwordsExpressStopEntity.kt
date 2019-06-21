package ie.dublinmapper.datamodel.swordsexpress

import androidx.room.*

data class SwordsExpressStopEntity(
    @field:Embedded val location: SwordsExpressStopLocationEntity
) {
    @field:Relation(parentColumn = "id", entityColumn = "stop_id")
    var services: List<SwordsExpressStopServiceEntity> = emptyList()
}

@Entity(tableName = "swordsexpress_stop_locations")
data class SwordsExpressStopLocationEntity(
    @field:PrimaryKey @field:ColumnInfo(name = "id") val id: String,
    @field:ColumnInfo(name = "name") val name: String,
    @field:ColumnInfo(name = "latitude") val latitude: Double,
    @field:ColumnInfo(name = "longitude") val longitude: Double
)

@Entity(
    tableName = "swordsexpress_stop_services",
    indices = [Index("stop_id")],
    foreignKeys = [
        ForeignKey(
            entity = SwordsExpressStopLocationEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("stop_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SwordsExpressStopServiceEntity(
    @field:PrimaryKey(autoGenerate = true) @field:ColumnInfo(name = "id") val id: Int = 0,
    @field:ColumnInfo(name = "stop_id") val stopId: String,
    @field:ColumnInfo(name = "operator") val operator: String,
    @field:ColumnInfo(name = "direction") val direction: String
)

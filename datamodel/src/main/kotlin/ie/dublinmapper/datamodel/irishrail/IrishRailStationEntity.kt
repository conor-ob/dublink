package ie.dublinmapper.datamodel.irishrail

import androidx.room.*

data class IrishRailStationEntity(
    @field:Embedded val location: IrishRailStationLocationEntity
) {
    @field:Relation(parentColumn = "id", entityColumn = "station_id")
    var services: List<IrishRailStationServiceEntity> = emptyList()
}

@Entity(tableName = "irishrail_station_locations")
data class IrishRailStationLocationEntity(
    @field:PrimaryKey @field:ColumnInfo(name = "id") val id: String,
    @field:ColumnInfo(name = "name") val name: String,
    @field:ColumnInfo(name = "latitude") val latitude: Double,
    @field:ColumnInfo(name = "longitude") val longitude: Double
)

@Entity(
    tableName = "irishrail_station_services",
    indices = [Index("station_id")],
    foreignKeys = [
        ForeignKey(
            entity = IrishRailStationLocationEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("station_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class IrishRailStationServiceEntity(
    @field:PrimaryKey(autoGenerate = true) @field:ColumnInfo(name = "id") val id: Int = 0,
    @field:ColumnInfo(name = "station_id") val stationId: String,
    @field:ColumnInfo(name = "operator") val operator: String
)

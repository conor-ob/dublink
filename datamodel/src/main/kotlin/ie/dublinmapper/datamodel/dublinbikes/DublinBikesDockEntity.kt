package ie.dublinmapper.datamodel.dublinbikes

import androidx.room.*

data class DublinBikesDockEntity(
    @field:Embedded val location: DublinBikesDockLocationEntity
) {
    @field:Relation(parentColumn = "id", entityColumn = "dock_id")
    var services: List<DublinBikesDockServiceEntity> = emptyList()
}

@Entity(tableName = "dublinbikes_dock_locations")
data class DublinBikesDockLocationEntity(
    @field:PrimaryKey @field:ColumnInfo(name = "id") val id: String,
    @field:ColumnInfo(name = "name") val name: String,
    @field:ColumnInfo(name = "latitude") val latitude: Double,
    @field:ColumnInfo(name = "longitude") val longitude: Double
)


@Entity(
    tableName = "dublinbikes_dock_services",
    indices = [Index("dock_id")],
    foreignKeys = [
        ForeignKey(
            entity = DublinBikesDockLocationEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("dock_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DublinBikesDockServiceEntity(
    @field:PrimaryKey(autoGenerate = true) @field:ColumnInfo(name = "id") val id: Int = 0,
    @field:ColumnInfo(name = "dock_id") val dockId: String,
    @field:ColumnInfo(name = "operator") val operator: String,
    @field:ColumnInfo(name = "docks") val docks: Int,
    @field:ColumnInfo(name = "available_bikes") val availableBikes: Int,
    @field:ColumnInfo(name = "available_docks") val availableDocks: Int
)

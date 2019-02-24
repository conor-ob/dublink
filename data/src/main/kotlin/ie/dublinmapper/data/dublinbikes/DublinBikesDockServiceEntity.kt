package ie.dublinmapper.data.dublinbikes

import androidx.room.*

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

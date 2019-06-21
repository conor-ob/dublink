package ie.dublinmapper.data.favourite

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import ie.dublinmapper.util.Operator

@Entity(
    tableName = "favourite_services",
    indices = [Index("location_id")],
    foreignKeys = [
        ForeignKey(
            entity = FavouriteLocationEntity::class,
            parentColumns = arrayOf("uuid"),
            childColumns = arrayOf("location_id"),
            onDelete = CASCADE
        )
    ]
)
data class FavouriteServiceEntity(
    @field:PrimaryKey(autoGenerate = true) @field:ColumnInfo(name = "id") val id: Int = 0,
    @field:ColumnInfo(name = "location_id") val locationId: String,
    @field:ColumnInfo(name = "operator") val operator: Operator,
    @field:ColumnInfo(name = "route") val route: String
)
package ie.dublinmapper.datamodel.favourite

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ie.dublinmapper.util.Service

@Entity(tableName = "favourite_locations")
data class FavouriteLocationEntity(
    @field:PrimaryKey(autoGenerate = true) @field:ColumnInfo(name = "uuid") val uuid: Int = 0,
    @field:ColumnInfo(name = "id") val id: String,
    @field:ColumnInfo(name = "name") val name: String,
    @field:ColumnInfo(name = "order") val order: Int,
    @field:ColumnInfo(name = "service") val service: Service
)

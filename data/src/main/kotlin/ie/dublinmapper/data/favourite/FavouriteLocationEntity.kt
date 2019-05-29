package ie.dublinmapper.data.favourite

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ie.dublinmapper.util.Service

@Entity(tableName = "favourite_locations")
data class FavouriteLocationEntity(
    @field:PrimaryKey @field:ColumnInfo(name = "id") val id: String, //TODO there might be collisions between services
    @field:ColumnInfo(name = "name") val name: String,
    @field:ColumnInfo(name = "service") val service: Service
)

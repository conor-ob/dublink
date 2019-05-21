package ie.dublinmapper.data.favourite

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ie.dublinmapper.util.Service
import java.util.*

@Entity(tableName = "favourites")
data class FavouriteEntity(
    @field:PrimaryKey @field:ColumnInfo(name = "uuid") val uuid: UUID = UUID.randomUUID(),
    @field:ColumnInfo(name = "id") val id: String,
    @field:ColumnInfo(name = "name") val name: String,
    @field:ColumnInfo(name = "service") val service: Service,
    @field:ColumnInfo(name = "routes") val routes: List<String>
)

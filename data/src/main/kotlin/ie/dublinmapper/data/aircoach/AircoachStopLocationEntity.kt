package ie.dublinmapper.data.aircoach

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "aircoach_stops")
data class AircoachStopLocationEntity(
    @field:ColumnInfo(name = "uuid") val uuid: UUID = UUID.randomUUID(),
    @field:PrimaryKey @field:ColumnInfo(name = "id") val id: String,
    @field:ColumnInfo(name = "name") val name: String,
    @field:ColumnInfo(name = "latitude") val latitude: Double,
    @field:ColumnInfo(name = "longitude") val longitude: Double
)

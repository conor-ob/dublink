package ie.dublinmapper.data.aircoach

import androidx.room.*
import java.util.*

@Entity(
    tableName = "aircoach_stop_services",
    indices = [Index("id")],
    foreignKeys = [
        ForeignKey(
            entity = AircoachStopLocationEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AircoachStopServiceEntity(
    @field:PrimaryKey @field:ColumnInfo(name = "uuid") val uuid: UUID = UUID.randomUUID(),
    @field:ColumnInfo(name = "id") val stopId: String,
    @field:ColumnInfo(name = "operator") val operator: String,
    @field:ColumnInfo(name = "route") val route: String
)

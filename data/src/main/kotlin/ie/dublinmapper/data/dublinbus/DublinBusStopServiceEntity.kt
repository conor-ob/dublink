package ie.dublinmapper.data.dublinbus

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import java.util.*

@Entity(
    tableName = "dublinbus_stop_services",
    indices = [Index("id")],
    foreignKeys = [
        ForeignKey(
            entity = DublinBusStopLocationEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("id"),
            onDelete = CASCADE
        )
    ]
)
data class DublinBusStopServiceEntity(
    @field:PrimaryKey @field:ColumnInfo(name = "uuid") val uuid: UUID = UUID.randomUUID(),
    @field:ColumnInfo(name = "id") val stopId: String,
    @field:ColumnInfo(name = "operator") val operator: String,
    @field:ColumnInfo(name = "route") val route: String
)

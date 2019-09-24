package ie.dublinmapper.datamodel.persister

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ie.dublinmapper.util.TimeUtils
import org.threeten.bp.Instant

@Entity(tableName = "persister_last_updated")
data class PersisterEntity(
    @field:PrimaryKey @field:ColumnInfo(name = "id") val id: String,
    @field:ColumnInfo(name = "last_updated") val lastUpdated: Instant = TimeUtils.now()
)

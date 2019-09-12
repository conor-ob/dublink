package ie.dublinmapper.datamodel.favourite

import androidx.room.*
import ie.dublinmapper.util.Operator
import ie.dublinmapper.util.Service

data class FavouriteKey(
    val serviceId: String,
    val service: Service
)

data class FavouriteEntity(
    @field:Embedded val location: FavouriteLocationEntity
) {
    @field:Relation(parentColumn = "id", entityColumn = "location_id")
    var services: List<FavouriteServiceEntity> = emptyList()
}

@Entity(tableName = "favourite_locations")
data class FavouriteLocationEntity(
    @field:PrimaryKey @field:ColumnInfo(name = "id") val id: FavouriteKey,
    @field:ColumnInfo(name = "name") val name: String,
    @field:ColumnInfo(name = "order") val order: Long
)

@Entity(
    tableName = "favourite_services",
    indices = [Index("location_id")],
    foreignKeys = [
        ForeignKey(
            entity = FavouriteLocationEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("location_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FavouriteServiceEntity(
    @field:PrimaryKey(autoGenerate = true) @field:ColumnInfo(name = "id") val id: Int = 0,
    @field:ColumnInfo(name = "location_id") val locationId: FavouriteKey,
    @field:ColumnInfo(name = "operator") val operator: Operator,
    @field:ColumnInfo(name = "route") val route: String
)

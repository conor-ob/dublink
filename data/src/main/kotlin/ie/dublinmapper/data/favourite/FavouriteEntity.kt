package ie.dublinmapper.data.favourite

import androidx.room.Embedded
import androidx.room.Relation

data class FavouriteEntity(
    @field:Embedded val location: FavouriteLocationEntity
) {
    @field:Relation(parentColumn = "id", entityColumn = "location_id")
    var services: List<FavouriteServiceEntity> = emptyList()
}
